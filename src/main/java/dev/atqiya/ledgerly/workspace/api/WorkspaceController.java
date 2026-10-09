package dev.atqiya.ledgerly.workspace.api;

import dev.atqiya.ledgerly.identity.application.AuthenticatedUser;
import dev.atqiya.ledgerly.workspace.application.WorkspaceService;
import dev.atqiya.ledgerly.workspace.application.WorkspaceView;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @PostMapping
    public ResponseEntity<WorkspaceResponse> create(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CreateWorkspaceRequest request
    ) {
        WorkspaceView workspace = workspaceService.create(
                user.id(),
                request.name(),
                request.defaultCurrency(),
                request.timezone()
        );
        return ResponseEntity
                .created(URI.create("/api/v1/workspaces/" + workspace.id()))
                .body(WorkspaceResponse.from(workspace));
    }

    @GetMapping
    public List<WorkspaceResponse> list(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return workspaceService.list(user.id())
                .stream()
                .map(WorkspaceResponse::from)
                .toList();
    }

    @GetMapping("/{workspaceId}")
    public WorkspaceResponse get(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID workspaceId
    ) {
        return WorkspaceResponse.from(
                workspaceService.get(user.id(), workspaceId)
        );
    }
}
