package dev.itltcanz.bankapi.service;

import dev.itltcanz.bankapi.entity.Card;
import dev.itltcanz.bankapi.exception.InactiveCardException;
import dev.itltcanz.bankapi.exception.InsufficientFundsException;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for managing card balance operations, such as fund transfers.
 */
@Service
@RequiredArgsConstructor
public class BalanceService {

  private final CardService cardService;
  private final CardCheckService cardCheckService;

  /**
   * Transfers funds from sender to receiver card.
   *
   * @param senderCard   The sender's card.
   * @param receiverCard The receiver's card.
   * @param amount       The amount to transfer.
   * @throws InactiveCardException      If either card is inactive.
   * @throws InsufficientFundsException If sender has insufficient funds.
   */
  public void transferFunds(Card senderCard, Card receiverCard, BigDecimal amount) {
    cardCheckService.checkValidityPeriod(senderCard.getValidityPeriod());
    cardCheckService.checkValidityPeriod(receiverCard.getValidityPeriod());

    cardCheckService.checkStatus(senderCard.getStatus());
    cardCheckService.checkStatus(receiverCard.getStatus());

    cardCheckService.checkBalanceBeforeTransfer(senderCard, amount);

    senderCard.setBalance(senderCard.getBalance().subtract(amount));
    receiverCard.setBalance(receiverCard.getBalance().add(amount));

    cardService.save(senderCard);
    cardService.save(receiverCard);
  }
}