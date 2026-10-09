package dev.atqiya.ledgerly.identity.application;

import java.util.UUID;

public record AuthenticatedUser(
        UUID id,
        String email,
        String displayName
) {
}