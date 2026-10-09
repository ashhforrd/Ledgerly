package dev.atqiya.ledgerly.workspace.application;

public final class WorkspaceNotFoundException extends RuntimeException {

    public WorkspaceNotFoundException() {
        super("The workspace could not be found.");
    }
}
