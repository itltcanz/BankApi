package dev.itltcanz.bankapi.service;

import dev.itltcanz.bankapi.dto.transaction.TransactionDtoCreate;
import dev.itltcanz.bankapi.dto.transaction.TransactionDtoResponse;
import dev.itltcanz.bankapi.entity.Transaction;
import dev.itltcanz.bankapi.exception.InactiveCardException;
import dev.itltcanz.bankapi.exception.InsufficientFundsException;
import dev.itltcanz.bankapi.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;

/**
 * Service for managing transaction entities.
 */
public interface TransactionService {

  /**
   * Creates a new transaction and transfers funds between cards.
   *
   * @param transactionDto The transaction details.
   * @return The created transaction details as a DTO.
   * @throws NotFoundException          if the sender or receiver card is not found.
   * @throws AccessDeniedException      if the user lacks access to the sender card.
   * @throws InactiveCardException      if either card is inactive or expired.
   * @throws InsufficientFundsException if the sender has insufficient funds.
   */
  TransactionDtoResponse createTransaction(TransactionDtoCreate transactionDto);

  TransactionDtoResponse getTransactionById(String transactionId);

  /**
   * Retrieves a paginated list of all transactions for admin users.
   *
   * @param pageable Pagination parameters.
   * @return A page of transaction details.
   */
  Page<TransactionDtoResponse> getAdminTransactions(PageRequest pageable);

  /**
   * Retrieves a paginated list of the authenticated user's transactions.
   *
   * @param pageable Pagination parameters.
   * @return A page of transaction details.
   */
  Page<TransactionDtoResponse> getUserTransactions(PageRequest pageable);

  /**
   * Retrieves a transaction by its ID with permission checks.
   *
   * @param transactionId The ID of the transaction.
   * @return The transaction details as a DTO.
   * @throws NotFoundException if the transaction is not found.
   */
  Transaction findByIdWithPermissionCheck(String transactionId);

  /**
   * Retrieves a transaction by its ID without permission checks.
   *
   * @param transactionId The ID of the transaction.
   * @return The transaction entity.
   * @throws NotFoundException if the transaction is not found.
   */
  Transaction findById(String transactionId);
}