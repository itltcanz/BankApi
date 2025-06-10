package dev.itltcanz.bankapi.service;

import dev.itltcanz.bankapi.entity.Card;
import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
import dev.itltcanz.bankapi.exception.InactiveCardException;
import dev.itltcanz.bankapi.exception.InsufficientFundsException;
import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * Service for validating card properties before operations.
 */
public interface CardCheckService {

  /**
   * Checks if the card's validity period is not expired.
   *
   * @param validityPeriod The card's validity period.
   * @throws InactiveCardException if the card is expired.
   */
  void checkValidityPeriod(YearMonth validityPeriod);

  /**
   * Checks if the card is active.
   *
   * @param cardStatus The card's status.
   * @throws InactiveCardException if the card is not active.
   */
  void checkStatus(CardStatus cardStatus);

  /**
   * Checks if the sender card has sufficient funds for a transfer.
   *
   * @param card   The sender's card.
   * @param amount The amount to transfer.
   * @throws InsufficientFundsException if the card has insufficient funds.
   */
  void checkBalanceBeforeTransfer(Card card, BigDecimal amount);
}
