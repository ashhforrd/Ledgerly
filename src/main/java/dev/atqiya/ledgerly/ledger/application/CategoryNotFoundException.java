package dev.atqiya.ledgerly.ledger.application;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException() {
        super("Category was not found.");
    }
}