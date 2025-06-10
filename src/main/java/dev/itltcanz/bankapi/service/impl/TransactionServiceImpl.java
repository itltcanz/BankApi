package dev.itltcanz.bankapi.service.impl;

import dev.itltcanz.bankapi.dto.transaction.TransactionDtoCreate;
import dev.itltcanz.bankapi.dto.transaction.TransactionDtoResponse;
import dev.itltcanz.bankapi.entity.Transaction;
import dev.itltcanz.bankapi.entity.enumeration.TransactionStatus;
import dev.itltcanz.bankapi.exception.NotFoundException;
import dev.itltcanz.bankapi.repository.TransactionRepo;
import dev.itltcanz.bankapi.service.AuthService;
import dev.itltcanz.bankapi.service.TransactionService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service("transactionService")
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

  private final TransactionRepo transactionRepo;
  private final CardServiceImpl cardService;
  private final AuthService authService;
  private final PermissionServiceImpl permissionService;
  private final BalanceServiceImpl balanceService;
  private final ModelMapper modelMapper;

  @Override
  @Transactional
  @CacheEvict(value = "{adminTransactions, userTransactions}")
  public TransactionDtoResponse createTransaction(TransactionDtoCreate transactionDto) {
    var senderCard = cardService.findByIdWithPermissionCheck(transactionDto.getSenderCardId());
    var receiverCard = cardService.findByIdWithPermissionCheck(transactionDto.getReceiverCardId());

    balanceService.transferFunds(senderCard, receiverCard, transactionDto.getAmount());

    var transaction = Transaction.builder().senderCard(senderCard).receiverCard(receiverCard)
        .amount(transactionDto.getAmount()).status(TransactionStatus.COMPLETED)
        .createdAt(LocalDateTime.now()).build();

    var savedTransaction = transactionRepo.save(transaction);
    return modelMapper.map(savedTransaction, TransactionDtoResponse.class);
  }

  @Override
  @Cacheable(value = "transaction", key = "#transactionId")
  public TransactionDtoResponse getTransactionById(String transactionId) {
    var transaction = findByIdWithPermissionCheck(transactionId);
    return modelMapper.map(transaction, TransactionDtoResponse.class);
  }

  @Override
  @Cacheable(
      value = "adminTransactions",
      key = "#pageable.pageNumber + '-' + #pageable.pageSize"
  )
  public Page<TransactionDtoResponse> getAdminTransactions(PageRequest pageable) {
    var transactions = transactionRepo.findAll(pageable);
    return transactions.map(
        transaction -> modelMapper.map(transaction, TransactionDtoResponse.class));
  }

  @Override
  @Cacheable(
      value = "userTransactions",
      key = "@authService.currentUser().id + '-' + #pageable.pageNumber + '-' + #pageable.pageSize"
  )
  public Page<TransactionDtoResponse> getUserTransactions(PageRequest pageable) {
    var currentUser = authService.getCurrentUser();
    var transactions = transactionRepo.findTransactionsBySenderCard_Owner(currentUser, pageable);
    return transactions.map(
        transaction -> modelMapper.map(transaction, TransactionDtoResponse.class));
  }

  @Override
  public Transaction findByIdWithPermissionCheck(String transactionId) {
    var transaction = findById(transactionId);
    permissionService.hasRights(transaction.getSenderCard().getOwner().getId().toString());
    return transaction;
  }

  @Override
  public Transaction findById(String transactionId) {
    return transactionRepo.findById(UUID.fromString(transactionId)).orElseThrow(
        () -> new NotFoundException(
            "A transaction with the number " + transactionId + " has not been found."));
  }
}