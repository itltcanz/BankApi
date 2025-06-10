package dev.itltcanz.bankapi.service.impl;

import dev.itltcanz.bankapi.repository.CardRepo;
import dev.itltcanz.bankapi.service.CardNumberGeneratorService;
import dev.itltcanz.bankapi.util.CardNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("cardNumberGeneratorService")
@RequiredArgsConstructor
public class CardNumberGeneratorServiceImpl implements CardNumberGeneratorService {

  private final CardRepo cardRepo;

  @Override
  public String generateCardNumber() {
    String cardNumber;
    do {
      cardNumber = CardNumberGenerator.generateCardNumber();
    } while (cardRepo.existsByNumber(cardNumber));
    return cardNumber;
  }
}
