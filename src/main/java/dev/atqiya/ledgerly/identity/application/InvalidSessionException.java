package dev.atqiya.ledgerly.identity.application;

public final class InvalidSessionException extends RuntimeException {

    public InvalidSessionException() {
        super("The authentication session is invalid or has expired.");
    }
}