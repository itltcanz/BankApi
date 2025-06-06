package dev.itltcanz.bankapi.service;

import dev.itltcanz.bankapi.repository.CardRepo;
import dev.itltcanz.bankapi.util.CardNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for generating unique card numbers.
 */
@Service
@RequiredArgsConstructor
public class CardNumberGeneratorService {

  private final CardRepo cardRepo;

  /**
   * Generates a unique card number.
   *
   * @return A unique card number.
   */
  public String generateCardNumber() {
    String cardNumber;
    do {
      cardNumber = CardNumberGenerator.generateCardNumber();
    } while (cardRepo.existsByNumber(cardNumber));
    return cardNumber;
  }
}
