package dev.itltcanz.bankapi.dto.card;

import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * Interface for Data Transfer Objects (DTOs) representing card details.
 */
public interface CardDto {

  /**
   * Retrieves the ID of the card owner.
   *
   * @return The owner's ID as a string.
   */
  String getOwnerId();

  /**
   * Retrieves the validity period of the card.
   *
   * @return The validity period as a YearMonth.
   */
  YearMonth getValidityPeriod();

  /**
   * Retrieves the balance of the card.
   *
   * @return The balance as a BigDecimal.
   */
  BigDecimal getBalance();
}