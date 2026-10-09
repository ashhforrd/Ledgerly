package dev.atqiya.ledgerly.identity.api;

import dev.atqiya.ledgerly.identity.application.AuthenticatedUser;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String displayName
) {

    public static UserResponse from(AuthenticatedUser user) {
        return new UserResponse(
                user.id(),
                user.email(),
                user.displayName()
        );
    }
}
