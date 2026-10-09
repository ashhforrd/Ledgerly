package dev.atqiya.ledgerly.ledger.application;
import dev.atqiya.ledgerly.ledger.domain.*;
import java.time.LocalDate;
import java.util.UUID;
public record TransactionFilter(LocalDate from, LocalDate to, TransactionType type,
        TransactionStatus status, UUID categoryId, String query) {}
