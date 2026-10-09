package dev.atqiya.ledgerly.ledger.application;

public class DuplicateCategoryException extends RuntimeException {

    public DuplicateCategoryException() {
        super("A category with the same name and type already exists.");
    }
}