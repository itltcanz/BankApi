package dev.itltcanz.bankapi.service;

/**
 * Service for generating unique card numbers.
 */
public interface CardNumberGeneratorService {

  /**
   * Generates a unique card number.
   *
   * @return A unique card number.
   */
  String generateCardNumber();
}