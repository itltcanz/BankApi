package dev.itltcanz.bankapi.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class NotFoundException extends RuntimeException {

  /**
   * Constructs a new NotFoundException with the specified message.
   *
   * @param message The detail message.
   */
  public NotFoundException(String message) {
    super(message);
  }
}