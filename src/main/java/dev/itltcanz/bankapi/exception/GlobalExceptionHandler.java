package dev.itltcanz.bankapi.exception;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for processing application exceptions and returning standardized error
 * responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * Handles NotFoundException and returns a 404 response.
   *
   * @param e The NotFoundException.
   * @return A ResponseEntity with an ErrorResponse and HTTP status 404.
   */
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
    log.error("Not found error: {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse("Not found", e.getMessage()));
  }

  /**
   * Handles AccessDeniedException and returns a 403 response.
   *
   * @param e The AccessDeniedException.
   * @return A ResponseEntity with an ErrorResponse and HTTP status 403.
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
    log.error("Access denied: {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ErrorResponse("Access denied", e.getMessage()));
  }

  /**
   * Handles IllegalStateException and returns a 400 response.
   *
   * @param e The IllegalStateException.
   * @return A ResponseEntity with an ErrorResponse and HTTP status 400.
   */
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
    log.error("Illegal state: {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("Bad request", e.getMessage()));
  }

  /**
   * Handles IllegalArgumentException and returns a 400 response.
   *
   * @param e The IllegalArgumentException.
   * @return A ResponseEntity with an ErrorResponse and HTTP status 400.
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
    log.error("Invalid argument: {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("Bad request", e.getMessage()));
  }

  /**
   * Handles UsernameAlreadyUseException and returns a 409 response.
   *
   * @param e The UsernameAlreadyUseException.
   * @return A ResponseEntity with an ErrorResponse and HTTP status 409.
   */
  @ExceptionHandler(UsernameAlreadyUseException.class)
  public ResponseEntity<ErrorResponse> handleUsernameAlreadyUseException(
      UsernameAlreadyUseException e) {
    log.error("Username already used: {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ErrorResponse("Conflict", e.getMessage()));
  }

  /**
   * Handles InactiveCardException and returns a 400 response.
   *
   * @param e The InactiveCardException.
   * @return A ResponseEntity with an ErrorResponse and HTTP status 400.
   */
  @ExceptionHandler(InactiveCardException.class)
  public ResponseEntity<ErrorResponse> handleInactiveCardException(InactiveCardException e) {
    log.error("Inactive card: {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("Bad request", e.getMessage()));
  }

  /**
   * Handles InsufficientFundsException and returns a 400 response.
   *
   * @param e The InsufficientFundsException.
   * @return A ResponseEntity with an ErrorResponse and HTTP status 400.
   */
  @ExceptionHandler(InsufficientFundsException.class)
  public ResponseEntity<ErrorResponse> handleInsufficientFundsException(
      InsufficientFundsException e) {
    log.error("Insufficient funds: {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("Bad request", e.getMessage()));
  }

  /**
   * Handles RequestAlreadyProcessedException and returns a 400 response.
   *
   * @param e The RequestAlreadyProcessedException.
   * @return A ResponseEntity with an ErrorResponse and HTTP status 400.
   */
  @ExceptionHandler(RequestAlreadyProcessedException.class)
  public ResponseEntity<ErrorResponse> handleRequestAlreadyProcessedException(
      RequestAlreadyProcessedException e) {
    log.error("Request already processed: {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("Bad request", e.getMessage()));
  }

  /**
   * Handles HttpMessageNotReadableException and returns a 400 response.
   *
   * @param e The HttpMessageNotReadableException.
   * @return A ResponseEntity with an ErrorResponse and HTTP status 400.
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException e) {
    log.error("Message not readable error: {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("Bad request", e.getMessage()));
  }

  /**
   * Handles MethodArgumentNotValidException and returns a 400 response.
   *
   * @param e The MethodArgumentNotValidException.
   * @return A ResponseEntity with an ErrorResponse and HTTP status 400.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    log.error("Validation error: {}", e.getMessage());
    String message = Objects.requireNonNull(e.getBindingResult().getFieldError())
        .getDefaultMessage();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("Bad request", message));
  }

  /**
   * Handles PropertyReferenceException and returns a 400 response.
   *
   * @param e The PropertyReferenceException.
   * @return A ResponseEntity with an ErrorResponse and HTTP status 400.
   */
  @ExceptionHandler(PropertyReferenceException.class)
  public ResponseEntity<ErrorResponse> handlePropertyReferenceException(
      PropertyReferenceException e) {
    log.error("Incorrect request: {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("Bad request", e.getMessage()));
  }

  /**
   * Handles generic exceptions and returns a 500 response.
   *
   * @param e The Exception.
   * @return A ResponseEntity with an ErrorResponse and HTTP status 500.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
    log.error("Unexpected error: {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse("Internal error", "An unexpected error occurred"));
  }
}