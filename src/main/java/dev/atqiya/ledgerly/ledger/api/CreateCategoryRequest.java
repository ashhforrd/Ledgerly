package dev.atqiya.ledgerly.ledger.api;

import dev.atqiya.ledgerly.ledger.domain.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank @Size(min = 2, max = 80) String name,
        @NotNull TransactionType type
) {
}
