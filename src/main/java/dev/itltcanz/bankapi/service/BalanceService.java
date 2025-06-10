package dev.itltcanz.bankapi.service;

import dev.itltcanz.bankapi.entity.Card;
import dev.itltcanz.bankapi.exception.InactiveCardException;
import dev.itltcanz.bankapi.exception.InsufficientFundsException;
import java.math.BigDecimal;

/**
 * Service for managing card balance operations, such as fund transfers.
 */
public interface BalanceService {

  /**
   * Transfers funds from sender to receiver card.
   *
   * @param senderCard   The sender's card.
   * @param receiverCard The receiver's card.
   * @param amount       The amount to transfer.
   * @throws InactiveCardException      If either card is inactive.
   * @throws InsufficientFundsException If sender has insufficient funds.
   */
  void transferFunds(Card senderCard, Card receiverCard, BigDecimal amount);
}