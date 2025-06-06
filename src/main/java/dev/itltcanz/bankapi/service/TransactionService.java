package dev.itltcanz.bankapi.service;

import dev.itltcanz.bankapi.dto.transaction.TransactionDtoCreate;
import dev.itltcanz.bankapi.dto.transaction.TransactionDtoResponse;
import dev.itltcanz.bankapi.entity.Transaction;
import dev.itltcanz.bankapi.entity.enumeration.TransactionStatus;
import dev.itltcanz.bankapi.exception.InactiveCardException;
import dev.itltcanz.bankapi.exception.InsufficientFundsException;
import dev.itltcanz.bankapi.exception.NotFoundException;
import dev.itltcanz.bankapi.repository.TransactionRepo;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

/**
 * Service for managing transaction entities.
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

  private final TransactionRepo transactionRepo;
  private final CardService cardService;
  private final AuthenticationService authService;
  private final PermissionService permissionService;
  private final BalanceService balanceService;
  private final ModelMapper modelMapper;

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
  @Transactional
  public TransactionDtoResponse createTransaction(TransactionDtoCreate transactionDto) {
    var senderCard = cardService.findByIdWithPermissionCheck(transactionDto.getSenderCardId());
    var receiverCard = cardService.findByIdWithPermissionCheck(transactionDto.getReceiverCardId());

    balanceService.transferFunds(senderCard, receiverCard, transactionDto.getAmount());

    var transaction = Transaction.builder()
        .senderCard(senderCard)
        .receiverCard(receiverCard)
        .amount(transactionDto.getAmount())
        .status(TransactionStatus.COMPLETED)
        .createdAt(LocalDateTime.now())
        .build();

    var savedTransaction = transactionRepo.save(transaction);
    return modelMapper.map(savedTransaction, TransactionDtoResponse.class);
  }

  /**
   * Retrieves a paginated list of all transactions for admin users.
   *
   * @param pageable Pagination parameters.
   * @return A page of transaction details.
   */
  public Page<TransactionDtoResponse> getTransactionsAdmin(PageRequest pageable) {
    var transactions = transactionRepo.findAll(pageable);
    return transactions
        .map(transaction -> modelMapper.map(transaction, TransactionDtoResponse.class));
  }

  /**
   * Retrieves a paginated list of the authenticated user's transactions.
   *
   * @param pageable Pagination parameters.
   * @return A page of transaction details.
   */
  public Page<TransactionDtoResponse> getTransactionsUser(PageRequest pageable) {
    var currentUser = authService.getCurrentUser();
    var transactions = transactionRepo.findTransactionsBySenderCard_Owner(currentUser, pageable);
    return transactions
        .map(transaction -> modelMapper.map(transaction, TransactionDtoResponse.class));
  }

  /**
   * Retrieves a transaction by its ID with permission checks.
   *
   * @param transactionId The ID of the transaction.
   * @return The transaction details as a DTO.
   * @throws NotFoundException if the transaction is not found.
   */
  public TransactionDtoResponse findByIdWithPermissionCheck(String transactionId) {
    var transaction = findById(transactionId);
    permissionService.hasRights(transaction.getSenderCard().getOwner().getId().toString());
    return modelMapper.map(transaction, TransactionDtoResponse.class);
  }

  /**
   * Retrieves a transaction by its ID without permission checks.
   *
   * @param transactionId The ID of the transaction.
   * @return The transaction entity.
   * @throws NotFoundException if the transaction is not found.
   */
  public Transaction findById(String transactionId) {
    return transactionRepo.findById(UUID.fromString(transactionId))
        .orElseThrow(() ->
            new NotFoundException(
                "A transaction with the number " + transactionId + " has not been found."));
  }
}