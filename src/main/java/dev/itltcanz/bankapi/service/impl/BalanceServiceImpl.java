package dev.itltcanz.bankapi.service.impl;

import dev.itltcanz.bankapi.entity.Card;
import dev.itltcanz.bankapi.service.BalanceService;
import dev.itltcanz.bankapi.service.CardCheckService;
import dev.itltcanz.bankapi.service.CardService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("balanceService")
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

  private final CardService cardService;
  private final CardCheckService cardCheckService;

  @Override
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