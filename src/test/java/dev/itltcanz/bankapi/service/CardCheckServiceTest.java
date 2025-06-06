package dev.itltcanz.bankapi.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.itltcanz.bankapi.entity.Card;
import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
import dev.itltcanz.bankapi.exception.InactiveCardException;
import dev.itltcanz.bankapi.exception.InsufficientFundsException;
import java.math.BigDecimal;
import java.time.YearMonth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CardCheckServiceTest {

  private CardCheckService cardCheckService;
  private Card card;

  @BeforeEach
  void setUp() {
    cardCheckService = new CardCheckService();
    card = new Card("1234567890123456", null, YearMonth.now().plusYears(1), CardStatus.ACTIVE,
        new BigDecimal("100"));
  }

  @Test
  void checkValidityPeriod_validPeriod_success() {
    YearMonth validPeriod = YearMonth.now().plusYears(1);

    assertDoesNotThrow(() -> cardCheckService.checkValidityPeriod(validPeriod));
  }

  @Test
  void checkValidityPeriod_expiredPeriod_throwsInactiveCardException() {
    YearMonth expiredPeriod = YearMonth.now().minusYears(1);

    InactiveCardException exception = assertThrows(InactiveCardException.class,
        () -> cardCheckService.checkValidityPeriod(expiredPeriod));

    assertEquals("Incorrect card expiration date", exception.getMessage());
  }

  @Test
  void checkStatus_activeStatus_success() {
    assertDoesNotThrow(() -> cardCheckService.checkStatus(CardStatus.ACTIVE));
  }

  @Test
  void checkStatus_inactiveStatus_throwsInactiveCardException() {
    InactiveCardException exception = assertThrows(InactiveCardException.class,
        () -> cardCheckService.checkStatus(CardStatus.BLOCKED));

    assertEquals("The card is inactive", exception.getMessage());
  }

  @Test
  void checkBalanceBeforeTransfer_sufficientFunds_success() {
    BigDecimal amount = new BigDecimal("50");

    assertDoesNotThrow(() -> cardCheckService.checkBalanceBeforeTransfer(card, amount));
  }

  @Test
  void checkBalanceBeforeTransfer_insufficientFunds_throwsInsufficientFundsException() {
    BigDecimal amount = new BigDecimal("200");

    InsufficientFundsException exception = assertThrows(InsufficientFundsException.class,
        () -> cardCheckService.checkBalanceBeforeTransfer(card, amount));

    assertEquals("There are insufficient funds on the 1234567890123456 card",
        exception.getMessage());
  }
}