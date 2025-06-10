package dev.itltcanz.bankapi.service;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.itltcanz.bankapi.dto.transaction.TransactionDtoCreate;
import dev.itltcanz.bankapi.dto.transaction.TransactionDtoResponse;
import dev.itltcanz.bankapi.entity.Card;
import dev.itltcanz.bankapi.entity.Transaction;
import dev.itltcanz.bankapi.entity.User;
import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
import dev.itltcanz.bankapi.entity.enumeration.Role;
import dev.itltcanz.bankapi.entity.enumeration.TransactionStatus;
import dev.itltcanz.bankapi.exception.NotFoundException;
import dev.itltcanz.bankapi.repository.TransactionRepo;
import dev.itltcanz.bankapi.service.impl.AuthServiceImpl;
import dev.itltcanz.bankapi.service.impl.BalanceServiceImpl;
import dev.itltcanz.bankapi.service.impl.CardServiceImpl;
import dev.itltcanz.bankapi.service.impl.PermissionServiceImpl;
import dev.itltcanz.bankapi.service.impl.TransactionServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

  @Mock
  private TransactionRepo transactionRepo;

  @Mock
  private CardServiceImpl cardService;

  @Mock
  private AuthServiceImpl authService;

  @Mock
  private PermissionServiceImpl permissionService;

  @Mock
  private BalanceServiceImpl balanceService;

  @Mock
  private ModelMapper modelMapper;

  @InjectMocks
  private TransactionServiceImpl transactionService;

  private Transaction transaction;
  private Card senderCard;
  private Card receiverCard;
  private User user;
  private TransactionDtoCreate transactionDto;
  private UUID transactionId;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(UUID.randomUUID());
    user.setRole(Role.ROLE_USER);

    senderCard = new Card("1234567890123456", user, YearMonth.now().plusYears(1), CardStatus.ACTIVE,
        new BigDecimal("100"));
    receiverCard = new Card("6543210987654321", user, YearMonth.now().plusYears(1),
        CardStatus.ACTIVE, new BigDecimal("50"));

    transactionId = UUID.randomUUID();
    transaction = Transaction.builder()
        .id(transactionId)
        .senderCard(senderCard)
        .receiverCard(receiverCard)
        .amount(new BigDecimal("30"))
        .status(TransactionStatus.COMPLETED)
        .createdAt(LocalDateTime.now())
        .build();

    transactionDto = new TransactionDtoCreate();
    transactionDto.setSenderCardId("1234567890123456");
    transactionDto.setReceiverCardId("6543210987654321");
    transactionDto.setAmount(new BigDecimal("30"));
  }

  @Test
  void createTransaction_success() {
    when(cardService.findByIdWithPermissionCheck("1234567890123456")).thenReturn(senderCard);
    when(cardService.findByIdWithPermissionCheck("6543210987654321")).thenReturn(receiverCard);
    when(transactionRepo.save(any(Transaction.class))).thenReturn(transaction);
    when(modelMapper.map(transaction, TransactionDtoResponse.class)).thenReturn(
        new TransactionDtoResponse());

    TransactionDtoResponse result = transactionService.createTransaction(transactionDto);

    assertNotNull(result);
    verify(balanceService).transferFunds(senderCard, receiverCard, transactionDto.getAmount());
    verify(transactionRepo).save(any(Transaction.class));
    verify(modelMapper).map(transaction, TransactionDtoResponse.class);
  }

  @Test
  void getAdmin_Transactions_success() {
    PageRequest pageable = PageRequest.of(0, 10);
    when(transactionRepo.findAll(pageable)).thenReturn(new PageImpl<>(List.of(transaction)));
    when(modelMapper.map(transaction, TransactionDtoResponse.class)).thenReturn(
        new TransactionDtoResponse());

    var result = transactionService.getAdminTransactions(pageable);

    assertEquals(1, result.getContent().size());
    verify(transactionRepo).findAll(pageable);
    verify(modelMapper).map(transaction, TransactionDtoResponse.class);
  }

  @Test
  void getUser_Transactions_success() {
    PageRequest pageable = PageRequest.of(0, 10);
    when(authService.getCurrentUser()).thenReturn(user);
    when(transactionRepo.findTransactionsBySenderCard_Owner(user, pageable)).thenReturn(
        new PageImpl<>(List.of(transaction)));
    when(modelMapper.map(transaction, TransactionDtoResponse.class)).thenReturn(
        new TransactionDtoResponse());

    var result = transactionService.getUserTransactions(pageable);

    assertEquals(1, result.getContent().size());
    verify(authService).getCurrentUser();
    verify(transactionRepo).findTransactionsBySenderCard_Owner(user, pageable);
    verify(modelMapper).map(transaction, TransactionDtoResponse.class);
  }

  @Test
  void findByIdWithPermissionCheck_success() {
    when(transactionRepo.findById(transactionId)).thenReturn(Optional.of(transaction));
    when(modelMapper.map(transaction, TransactionDtoResponse.class)).thenReturn(
        new TransactionDtoResponse());

    TransactionDtoResponse result = transactionService.getTransactionById(
        transactionId.toString());

    assertNotNull(result);
    verify(permissionService).hasRights(user.getId().toString());
    verify(modelMapper).map(transaction, TransactionDtoResponse.class);
  }

  @Test
  void findById_success() {
    when(transactionRepo.findById(transactionId)).thenReturn(Optional.of(transaction));

    Transaction result = transactionService.findById(transactionId.toString());

    assertEquals(transaction, result);
    verify(transactionRepo).findById(transactionId);
  }

  @Test
  void findById_notFound_throwsNotFoundException() {
    when(transactionRepo.findById(transactionId)).thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class,
        () -> transactionService.findById(transactionId.toString()));

    assertEquals("A transaction with the number " + transactionId + " has not been found.",
        exception.getMessage());
    verify(transactionRepo).findById(transactionId);
  }
}