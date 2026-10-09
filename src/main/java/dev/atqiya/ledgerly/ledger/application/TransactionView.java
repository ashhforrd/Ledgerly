package dev.atqiya.ledgerly.ledger.application;

import dev.atqiya.ledgerly.ledger.domain.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;

public record TransactionView(UUID id, UUID workspaceId, UUID categoryId, String categoryName,
        TransactionType type, TransactionStatus status, BigDecimal amount, String currency,
        LocalDate occurredOn, String description, String notes, UUID createdBy, UUID updatedBy,
        UUID voidedBy, Instant voidedAt, String voidReason, Instant createdAt, Instant updatedAt,
        long version) {
    public static TransactionView from(LedgerTransaction value) {
        return new TransactionView(value.getId(), value.getWorkspaceId(), value.getCategoryId(),
                value.getCategoryName(), value.getType(), value.getStatus(), value.getAmount(),
                value.getCurrency(), value.getOccurredOn(), value.getDescription(), value.getNotes(),
                value.getCreatedById(), value.getUpdatedById(), value.getVoidedById(), value.getVoidedAt(),
                value.getVoidReason(), value.getCreatedAt(), value.getUpdatedAt(), value.getVersion());
    }
}
