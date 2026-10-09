package dev.atqiya.ledgerly.identity.application;

public final class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("The email or password is incorrect.");
    }
}