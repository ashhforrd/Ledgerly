package dev.atqiya.ledgerly.identity.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Email is required.")
        @Email(message = "Email must be valid.")
        @Size(max = 320, message = "Email must not exceed 320 characters.")
        String email,

        @NotBlank(message = "Display name is required.")
        @Size(
                min = 2,
                max = 120,
                message = "Display name must contain between 2 and 120 characters."
        )
        String displayName,

        @NotBlank(message = "Password is required.")
        @Size(
                min = 12,
                max = 72,
                message = "Password must contain between 12 and 72 characters."
        )
        String password
) {
}