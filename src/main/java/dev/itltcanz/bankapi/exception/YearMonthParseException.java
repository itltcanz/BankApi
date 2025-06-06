package dev.itltcanz.bankapi.exception;

/**
 * Exception thrown when a YearMonth cannot be parsed from a string.
 */
public class YearMonthParseException extends RuntimeException {

  /**
   * Constructs a new YearMonthParseException with the specified message and cause.
   *
   * @param message The detail message.
   * @param cause   The cause of the exception.
   */
  public YearMonthParseException(String message, Throwable cause) {
    super(message, cause);
  }
}