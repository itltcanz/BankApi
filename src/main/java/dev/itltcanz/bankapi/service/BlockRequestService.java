package dev.itltcanz.bankapi.service;

import dev.itltcanz.bankapi.dto.request.BlockRequestDtoCreate;
import dev.itltcanz.bankapi.dto.request.BlockRequestDtoResponse;
import dev.itltcanz.bankapi.entity.BlockRequest;
import dev.itltcanz.bankapi.entity.enumeration.RequestStatus;
import dev.itltcanz.bankapi.exception.NotFoundException;
import dev.itltcanz.bankapi.exception.RequestAlreadyProcessedException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Service for managing card block requests.
 */
public interface BlockRequestService {

  /**
   * Retrieves a card block request by its ID.
   *
   * @param requestId The ID of the block request.
   * @return The block request details as a DTO.
   * @throws NotFoundException if the block request is not found.
   */
  BlockRequestDtoResponse getRequest(String requestId);

  /**
   * Creates a new card block request.
   *
   * @param dto The block request creation details.
   * @return The created block request as a DTO.
   * @throws NotFoundException     if the card is not found.
   * @throws IllegalStateException if the card is already blocked.
   */
  BlockRequestDtoResponse createRequest(BlockRequestDtoCreate dto);

  /**
   * Approves a card block request and blocks the associated card.
   *
   * @param requestId The ID of the block request.
   * @return The updated block request as a DTO.
   * @throws NotFoundException                if the block request or card is not found.
   * @throws RequestAlreadyProcessedException if the request is not pending.
   */
  @Transactional
  BlockRequestDtoResponse approveRequest(String requestId);

  /**
   * Rejects a card block request.
   *
   * @param requestId The ID of the block request.
   * @return The updated block request as a DTO.
   * @throws NotFoundException                if the block request is not found.
   * @throws RequestAlreadyProcessedException if the request is not pending.
   */
  @Transactional
  BlockRequestDtoResponse rejectRequest(@NotNull String requestId);

  /**
   * Retrieves a block request by ID with permission checks.
   *
   * @param requestId The ID of the block request.
   * @return The block request entity.
   * @throws NotFoundException if the block request is not found.
   */
  BlockRequest findByIdWithPermissionCheck(String requestId);

  /**
   * Checks if the block request is in a pending state.
   *
   * @param requestStatus The status of the block request.
   * @throws RequestAlreadyProcessedException if the request is not pending.
   */
  void checkRequestStatus(RequestStatus requestStatus);

  /**
   * Retrieves a paginated list of block requests for the authenticated user.
   *
   * @param pageable Pagination parameters.
   * @return A page of block request details.
   */
  Page<BlockRequestDtoResponse> getRequestsUser(PageRequest pageable);

  /**
   * Retrieves a paginated list of all block requests for admin users.
   *
   * @param pageable Pagination parameters.
   * @return A page of block request details.
   */
  Page<BlockRequestDtoResponse> getRequestsAdmin(PageRequest pageable);
}