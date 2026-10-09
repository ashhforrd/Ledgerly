package dev.atqiya.ledgerly.ledger.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RenameCategoryRequest(
        @NotBlank @Size(min = 2, max = 80) String name
) {
}
