package dev.itltcanz.bankapi.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import dev.itltcanz.bankapi.entity.Card;
import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
import dev.itltcanz.bankapi.exception.InsufficientFundsException;
import java.math.BigDecimal;
import java.time.YearMonth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

  @Mock
  private CardService cardService;

  @Mock
  private CardCheckService cardCheckService;

  @InjectMocks
  private BalanceService balanceService;

  private Card senderCard;
  private Card receiverCard;

  @BeforeEach
  void setUp() {
    senderCard = new Card("1234567890123456", null,
        YearMonth.now().plusYears(1), CardStatus.ACTIVE, new BigDecimal("100"));
    receiverCard = new Card("6543210987654321", null,
        YearMonth.now().plusYears(1), CardStatus.ACTIVE, new BigDecimal("50"));
  }

  @Test
  void transferFunds_success() {
    BigDecimal amount = new BigDecimal("30");

    balanceService.transferFunds(senderCard, receiverCard, amount);

    assertEquals(new BigDecimal("70"), senderCard.getBalance());
    assertEquals(new BigDecimal("80"), receiverCard.getBalance());
    verify(cardCheckService, times(2)).checkValidityPeriod(any(YearMonth.class));
    verify(cardCheckService, times(2)).checkStatus(CardStatus.ACTIVE);
    verify(cardCheckService).checkBalanceBeforeTransfer(senderCard, amount);
    verify(cardService, times(2)).save(any(Card.class));
  }

  @Test
  void transferFunds_insufficientFunds_throwsInsufficientFundsException() {
    BigDecimal amount = new BigDecimal("200");
    doThrow(new InsufficientFundsException("Insufficient funds")).when(cardCheckService)
        .checkBalanceBeforeTransfer(senderCard, amount);

    InsufficientFundsException exception = assertThrows(InsufficientFundsException.class,
        () -> balanceService.transferFunds(senderCard, receiverCard, amount));

    assertEquals("Insufficient funds", exception.getMessage());
    verify(cardCheckService).checkBalanceBeforeTransfer(senderCard, amount);
    verifyNoInteractions(cardService);
  }
}