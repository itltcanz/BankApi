package dev.itltcanz.bankapi.exception;

/**
 * Exception thrown when a card has insufficient funds for a transaction.
 */
public class InsufficientFundsException extends RuntimeException {

  /**
   * Constructs a new InsufficientFundsException with the specified message.
   *
   * @param message The detail message.
   */
  public InsufficientFundsException(String message) {
    super(message);
  }
}