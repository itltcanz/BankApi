package dev.itltcanz.bankapi.service;

import dev.itltcanz.bankapi.dto.request.BlockRequestDtoCreate;
import dev.itltcanz.bankapi.dto.request.BlockRequestDtoResponse;
import dev.itltcanz.bankapi.entity.BlockRequest;
import dev.itltcanz.bankapi.entity.Card;
import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
import dev.itltcanz.bankapi.entity.enumeration.RequestStatus;
import dev.itltcanz.bankapi.exception.NotFoundException;
import dev.itltcanz.bankapi.exception.RequestAlreadyProcessedException;
import dev.itltcanz.bankapi.repository.BlockRequestRepo;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * Service for managing card block requests.
 */
@Service
@RequiredArgsConstructor
public class BlockRequestService {

  private final BlockRequestRepo requestRepo;
  private final CardService cardService;
  private final AuthenticationService authService;
  private final PermissionService permissionService;
  private final ModelMapper modelMapper;

  /**
   * Retrieves a card block request by its ID.
   *
   * @param requestId The ID of the block request.
   * @return The block request details as a DTO.
   * @throws NotFoundException if the block request is not found.
   */
  public BlockRequestDtoResponse getRequest(String requestId) {
    var blockingRequest = findByIdWithPermissionCheck(requestId);
    return modelMapper.map(blockingRequest, BlockRequestDtoResponse.class);
  }

  /**
   * Creates a new card block request.
   *
   * @param dto The block request creation details.
   * @return The created block request as a DTO.
   * @throws NotFoundException     if the card is not found.
   * @throws IllegalStateException if the card is already blocked.
   */
  public BlockRequestDtoResponse createRequest(BlockRequestDtoCreate dto) {
    var card = cardService.findByIdWithPermissionCheck(dto.getCardId());
    if (card.getStatus().equals(CardStatus.BLOCKED)) {
      throw new IllegalStateException("The card has already been blocked");
    }
    var request = new BlockRequest();
    request.setCard(card);
    request.setUser(authService.getCurrentUser());
    var savedRequest = requestRepo.save(request);
    return modelMapper.map(savedRequest, BlockRequestDtoResponse.class);
  }

  /**
   * Approves a card block request and blocks the associated card.
   *
   * @param requestId The ID of the block request.
   * @return The updated block request as a DTO.
   * @throws NotFoundException                if the block request or card is not found.
   * @throws RequestAlreadyProcessedException if the request is not pending.
   */
  @Transactional
  public BlockRequestDtoResponse approveRequest(String requestId) {
    var request = findByIdWithPermissionCheck(requestId);
    checkRequestStatus(request.getStatus());
    Card card = cardService.findByIdWithPermissionCheck(request.getCard().getNumber());
    card.setStatus(CardStatus.BLOCKED);
    request.setStatus(RequestStatus.APPROVED);
    request.setAdmin(authService.getCurrentUser());
    request.setUpdatedAt(LocalDateTime.now());
    cardService.save(card);
    var savedRequest = requestRepo.save(request);
    return modelMapper.map(savedRequest, BlockRequestDtoResponse.class);
  }

  /**
   * Rejects a card block request.
   *
   * @param requestId The ID of the block request.
   * @return The updated block request as a DTO.
   * @throws NotFoundException                if the block request is not found.
   * @throws RequestAlreadyProcessedException if the request is not pending.
   */
  @Transactional
  public BlockRequestDtoResponse rejectRequest(@NotNull String requestId) {
    var request = findByIdWithPermissionCheck(requestId);
    checkRequestStatus(request.getStatus());
    request.setStatus(RequestStatus.REJECTED);
    request.setAdmin(authService.getCurrentUser());
    request.setUpdatedAt(LocalDateTime.now());
    var savedRequest = requestRepo.save(request);
    return modelMapper.map(savedRequest, BlockRequestDtoResponse.class);
  }

  /**
   * Retrieves a block request by ID with permission checks.
   *
   * @param requestId The ID of the block request.
   * @return The block request entity.
   * @throws NotFoundException if the block request is not found.
   */
  public BlockRequest findByIdWithPermissionCheck(String requestId) {
    var request = requestRepo.findById(UUID.fromString(requestId))
        .orElseThrow(
            () -> new NotFoundException("A block request with id " + requestId + " was not found"));
    permissionService.hasRights(request.getUser().getId().toString());
    return request;
  }

  /**
   * Checks if the block request is in a pending state.
   *
   * @param requestStatus The status of the block request.
   * @throws RequestAlreadyProcessedException if the request is not pending.
   */
  public void checkRequestStatus(RequestStatus requestStatus) {
    if (requestStatus != RequestStatus.PENDING) {
      throw new RequestAlreadyProcessedException("The request has already been processed");
    }
  }

  /**
   * Retrieves a paginated list of block requests for the authenticated user.
   *
   * @param pageable Pagination parameters.
   * @return A page of block request details.
   */
  public Page<BlockRequestDtoResponse> getRequestsUser(PageRequest pageable) {
    var currentUser = authService.getCurrentUser();
    return requestRepo.findAllByUser(currentUser, pageable)
        .map(request -> modelMapper.map(request, BlockRequestDtoResponse.class));
  }

  /**
   * Retrieves a paginated list of all block requests for admin users.
   *
   * @param pageable Pagination parameters.
   * @return A page of block request details.
   */
  public Page<BlockRequestDtoResponse> getRequestsAdmin(PageRequest pageable) {
    return requestRepo.findAll(pageable)
        .map(request -> modelMapper.map(request, BlockRequestDtoResponse.class));
  }
}