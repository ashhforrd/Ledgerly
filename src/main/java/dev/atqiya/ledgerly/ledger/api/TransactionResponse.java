package dev.atqiya.ledgerly.ledger.api;
import dev.atqiya.ledgerly.ledger.application.TransactionView;
import dev.atqiya.ledgerly.ledger.domain.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;
public record TransactionResponse(UUID id, UUID workspaceId, UUID categoryId, String categoryName,
        TransactionType type, TransactionStatus status, BigDecimal amount, String currency,
        LocalDate occurredOn, String description, String notes, UUID createdBy, UUID updatedBy,
        UUID voidedBy, Instant voidedAt, String voidReason, Instant createdAt, Instant updatedAt,
        long version) {
    public static TransactionResponse from(TransactionView v) {
        return new TransactionResponse(v.id(), v.workspaceId(), v.categoryId(), v.categoryName(), v.type(),
                v.status(), v.amount(), v.currency(), v.occurredOn(), v.description(), v.notes(),
                v.createdBy(), v.updatedBy(), v.voidedBy(), v.voidedAt(), v.voidReason(),
                v.createdAt(), v.updatedAt(), v.version());
    }
}
