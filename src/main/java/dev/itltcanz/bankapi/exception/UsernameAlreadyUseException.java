package dev.itltcanz.bankapi.exception;

/**
 * Exception thrown when a username is already in use.
 */
public class UsernameAlreadyUseException extends RuntimeException {

  /**
   * Constructs a new UsernameAlreadyUseException with the specified message.
   *
   * @param message The detail message.
   */
  public UsernameAlreadyUseException(String message) {
    super(message);
  }
}