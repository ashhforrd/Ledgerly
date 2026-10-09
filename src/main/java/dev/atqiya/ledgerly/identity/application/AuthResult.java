package dev.atqiya.ledgerly.identity.application;

import java.time.Instant;

public record AuthResult(
        String accessToken,
        Instant expiresAt,
        AuthenticatedUser user
) {
}