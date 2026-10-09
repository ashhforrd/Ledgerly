package dev.atqiya.ledgerly.workspace.application;

import dev.atqiya.ledgerly.workspace.domain.WorkspaceMembership;
import dev.atqiya.ledgerly.workspace.domain.WorkspaceRole;
import dev.atqiya.ledgerly.workspace.infrastructure.WorkspaceMembershipRepository;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkspaceAccessService {

    private final WorkspaceMembershipRepository membershipRepository;

    public WorkspaceAccessService(
            WorkspaceMembershipRepository membershipRepository
    ) {
        this.membershipRepository = membershipRepository;
    }

    @Transactional(readOnly = true)
    public WorkspaceMembership requireMembership(UUID userId, UUID workspaceId) {
        return membershipRepository
                .findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(WorkspaceNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public WorkspaceMembership requireRole(
            UUID userId,
            UUID workspaceId,
            Set<WorkspaceRole> allowedRoles
    ) {
        WorkspaceMembership membership = requireMembership(userId, workspaceId);
        if (!allowedRoles.contains(membership.getRole())) {
            throw new WorkspaceNotFoundException();
        }
        return membership;
    }
}
