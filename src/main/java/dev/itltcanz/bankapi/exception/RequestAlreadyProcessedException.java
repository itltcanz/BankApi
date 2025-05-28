package dev.itltcanz.bankapi.exception;

public class RequestAlreadyProcessedException extends RuntimeException {
    public RequestAlreadyProcessedException(String message) {
        super(message);
    }
}
