package dev.itltcanz.bankapi.service;

import dev.itltcanz.bankapi.entity.Card;
import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
import dev.itltcanz.bankapi.exception.InactiveCardException;
import dev.itltcanz.bankapi.exception.InsufficientFundsException;
import java.math.BigDecimal;
import java.time.YearMonth;
import org.springframework.stereotype.Service;

/**
 * Service for validating card properties before operations.
 */
@Service
public class CardCheckService {

  /**
   * Checks if the card's validity period is not expired.
   *
   * @param validityPeriod The card's validity period.
   * @throws InactiveCardException if the card is expired.
   */
  public void checkValidityPeriod(YearMonth validityPeriod) {
    if (validityPeriod.isBefore(YearMonth.now())) {
      throw new InactiveCardException("Incorrect card expiration date");
    }
  }

  /**
   * Checks if the card is active.
   *
   * @param cardStatus The card's status.
   * @throws InactiveCardException if the card is not active.
   */
  public void checkStatus(CardStatus cardStatus) {
    if (!cardStatus.equals(CardStatus.ACTIVE)) {
      throw new InactiveCardException("The card is inactive");
    }
  }

  /**
   * Checks if the sender card has sufficient funds for a transfer.
   *
   * @param card   The sender's card.
   * @param amount The amount to transfer.
   * @throws InsufficientFundsException if the card has insufficient funds.
   */
  public void checkBalanceBeforeTransfer(Card card, BigDecimal amount) {
    if (card.getBalance().compareTo(amount) < 0) {
      throw new InsufficientFundsException(
          "There are insufficient funds on the " + card.getNumber() + " card");
    }
  }
}
