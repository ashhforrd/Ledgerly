package dev.atqiya.ledgerly.workspace.api;

import dev.atqiya.ledgerly.workspace.application.WorkspaceView;
import dev.atqiya.ledgerly.workspace.domain.WorkspaceRole;
import java.util.UUID;

public record WorkspaceResponse(
        UUID id,
        String name,
        String slug,
        String defaultCurrency,
        String timezone,
        WorkspaceRole role
) {

    public static WorkspaceResponse from(WorkspaceView view) {
        return new WorkspaceResponse(
                view.id(),
                view.name(),
                view.slug(),
                view.defaultCurrency(),
                view.timezone(),
                view.role()
        );
    }
}
