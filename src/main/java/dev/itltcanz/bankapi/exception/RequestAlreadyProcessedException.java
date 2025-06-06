package dev.itltcanz.bankapi.exception;

/**
 * Exception thrown when a request has already been processed.
 */
public class RequestAlreadyProcessedException extends RuntimeException {

  /**
   * Constructs a new RequestAlreadyProcessedException with the specified message.
   *
   * @param message The detail message.
   */
  public RequestAlreadyProcessedException(String message) {
    super(message);
  }
}