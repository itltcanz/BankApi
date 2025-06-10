package dev.itltcanz.bankapi.service.impl;

import dev.itltcanz.bankapi.dto.request.BlockRequestDtoCreate;
import dev.itltcanz.bankapi.dto.request.BlockRequestDtoResponse;
import dev.itltcanz.bankapi.entity.BlockRequest;
import dev.itltcanz.bankapi.entity.Card;
import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
import dev.itltcanz.bankapi.entity.enumeration.RequestStatus;
import dev.itltcanz.bankapi.exception.NotFoundException;
import dev.itltcanz.bankapi.exception.RequestAlreadyProcessedException;
import dev.itltcanz.bankapi.repository.BlockRequestRepo;
import dev.itltcanz.bankapi.service.AuthService;
import dev.itltcanz.bankapi.service.BlockRequestService;
import dev.itltcanz.bankapi.service.CardService;
import dev.itltcanz.bankapi.service.PermissionService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service("blockRequestService")
@RequiredArgsConstructor
public class BlockRequestServiceImpl implements BlockRequestService {

  private final BlockRequestRepo requestRepo;
  private final CardService cardService;
  private final AuthService authService;
  private final PermissionService permissionService;
  private final ModelMapper modelMapper;

  @Override
  public BlockRequestDtoResponse getRequest(String requestId) {
    var blockingRequest = findByIdWithPermissionCheck(requestId);
    return modelMapper.map(blockingRequest, BlockRequestDtoResponse.class);
  }

  @Override
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

  @Override
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

  @Override
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

  @Override
  public BlockRequest findByIdWithPermissionCheck(String requestId) {
    var request = requestRepo.findById(UUID.fromString(requestId))
        .orElseThrow(
            () -> new NotFoundException("A block request with id " + requestId + " was not found"));
    permissionService.hasRights(request.getUser().getId().toString());
    return request;
  }

  @Override
  public void checkRequestStatus(RequestStatus requestStatus) {
    if (requestStatus != RequestStatus.PENDING) {
      throw new RequestAlreadyProcessedException("The request has already been processed");
    }
  }

  @Override
  public Page<BlockRequestDtoResponse> getRequestsUser(PageRequest pageable) {
    var currentUser = authService.getCurrentUser();
    return requestRepo.findAllByUser(currentUser, pageable)
        .map(request -> modelMapper.map(request, BlockRequestDtoResponse.class));
  }

  @Override
  public Page<BlockRequestDtoResponse> getRequestsAdmin(PageRequest pageable) {
    return requestRepo.findAll(pageable)
        .map(request -> modelMapper.map(request, BlockRequestDtoResponse.class));
  }
}