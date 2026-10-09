package dev.atqiya.ledgerly.workspace.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateWorkspaceRequest(
        @NotBlank(message = "Workspace name is required.")
        @Size(min = 2, max = 120,
                message = "Workspace name must contain between 2 and 120 characters.")
        String name,

        @NotBlank(message = "Currency is required.")
        @Pattern(regexp = "^[A-Za-z]{3}$",
                message = "Currency must be a three-letter ISO code.")
        String defaultCurrency,

        @NotBlank(message = "Timezone is required.")
        @Size(max = 80, message = "Timezone must not exceed 80 characters.")
        String timezone
) {
}
