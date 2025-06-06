package dev.itltcanz.bankapi.exception;

/**
 * Exception thrown when a card is inactive or expired.
 */
public class InactiveCardException extends RuntimeException {

  /**
   * Constructs a new InactiveCardException with the specified message.
   *
   * @param message The detail message.
   */
  public InactiveCardException(String message) {
    super(message);
  }
}