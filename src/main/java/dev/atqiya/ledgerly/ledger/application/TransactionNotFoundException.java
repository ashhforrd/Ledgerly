package dev.atqiya.ledgerly.ledger.application;
public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException() { super("Transaction was not found."); }
}
