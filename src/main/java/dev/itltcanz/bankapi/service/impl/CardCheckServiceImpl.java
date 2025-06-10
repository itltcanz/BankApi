package dev.itltcanz.bankapi.service.impl;

import dev.itltcanz.bankapi.entity.Card;
import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
import dev.itltcanz.bankapi.exception.InactiveCardException;
import dev.itltcanz.bankapi.exception.InsufficientFundsException;
import dev.itltcanz.bankapi.service.CardCheckService;
import java.math.BigDecimal;
import java.time.YearMonth;
import org.springframework.stereotype.Service;

@Service("cardCheckService")
public class CardCheckServiceImpl implements CardCheckService {

  @Override
  public void checkValidityPeriod(YearMonth validityPeriod) {
    if (validityPeriod.isBefore(YearMonth.now())) {
      throw new InactiveCardException("Incorrect card expiration date");
    }
  }

  @Override
  public void checkStatus(CardStatus cardStatus) {
    if (!cardStatus.equals(CardStatus.ACTIVE)) {
      throw new InactiveCardException("The card is inactive");
    }
  }

  @Override
  public void checkBalanceBeforeTransfer(Card card, BigDecimal amount) {
    if (card.getBalance().compareTo(amount) < 0) {
      throw new InsufficientFundsException(
          "There are insufficient funds on the " + card.getNumber() + " card");
    }
  }
}
