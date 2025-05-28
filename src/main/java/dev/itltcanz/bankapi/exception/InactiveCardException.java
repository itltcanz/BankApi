package dev.itltcanz.bankapi.exception;

public class InactiveCardException extends RuntimeException {
    public InactiveCardException(String message) {
        super(message);
    }
}
