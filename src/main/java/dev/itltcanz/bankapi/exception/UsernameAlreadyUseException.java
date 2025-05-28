package dev.itltcanz.bankapi.exception;

public class UsernameAlreadyUseException extends RuntimeException {
    public UsernameAlreadyUseException(String message) {
        super(message);
    }
}
