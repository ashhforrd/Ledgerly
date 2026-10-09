package dev.atqiya.ledgerly.ledger.application;
public class InvalidTransactionException extends RuntimeException {
    public InvalidTransactionException(String message) { super(message); }
}
