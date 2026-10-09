package dev.atqiya.ledgerly.identity.api;

import dev.atqiya.ledgerly.identity.application.AuthResult;
import java.time.Instant;

public record AuthResponse(
        String accessToken,
        String tokenType,
        Instant expiresAt,
        UserResponse user
) {

    public static AuthResponse from(AuthResult result) {
        return new AuthResponse(
                result.accessToken(),
                "Bearer",
                result.expiresAt(),
                UserResponse.from(result.user())
        );
    }
}
