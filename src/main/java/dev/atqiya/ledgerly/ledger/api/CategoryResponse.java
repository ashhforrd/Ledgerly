package dev.atqiya.ledgerly.ledger.api;

import dev.atqiya.ledgerly.ledger.application.CategoryView;
import dev.atqiya.ledgerly.ledger.domain.TransactionType;
import java.time.Instant;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        UUID workspaceId,
        String name,
        TransactionType type,
        boolean active,
        UUID createdBy,
        UUID updatedBy,
        Instant createdAt,
        Instant updatedAt,
        long version
) {
    public static CategoryResponse from(CategoryView view) {
        return new CategoryResponse(
                view.id(), view.workspaceId(), view.name(), view.type(),
                view.active(), view.createdBy(), view.updatedBy(),
                view.createdAt(), view.updatedAt(), view.version()
        );
    }
}
