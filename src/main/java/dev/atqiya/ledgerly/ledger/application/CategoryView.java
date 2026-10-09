package dev.atqiya.ledgerly.ledger.application;

import dev.atqiya.ledgerly.ledger.domain.Category;
import dev.atqiya.ledgerly.ledger.domain.TransactionType;
import java.time.Instant;
import java.util.UUID;

public record CategoryView(
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

    public static CategoryView from(Category category) {
        return new CategoryView(
                category.getId(),
                category.getWorkspaceId(),
                category.getName(),
                category.getType(),
                category.isActive(),
                category.getCreatedById(),
                category.getUpdatedById(),
                category.getCreatedAt(),
                category.getUpdatedAt(),
                category.getVersion()
        );
    }
}