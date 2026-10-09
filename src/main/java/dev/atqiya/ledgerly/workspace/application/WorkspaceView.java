package dev.atqiya.ledgerly.workspace.application;

import dev.atqiya.ledgerly.workspace.domain.Workspace;
import dev.atqiya.ledgerly.workspace.domain.WorkspaceRole;
import java.util.UUID;

public record WorkspaceView(
        UUID id,
        String name,
        String slug,
        String defaultCurrency,
        String timezone,
        WorkspaceRole role
) {

    public static WorkspaceView from(Workspace workspace, WorkspaceRole role) {
        return new WorkspaceView(
                workspace.getId(),
                workspace.getName(),
                workspace.getSlug(),
                workspace.getDefaultCurrency(),
                workspace.getTimezone(),
                role
        );
    }
}
