package dev.atqiya.ledgerly.workspace.infrastructure;

import dev.atqiya.ledgerly.workspace.domain.WorkspaceMembership;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceMembershipRepository extends JpaRepository<WorkspaceMembership, UUID> {

	@EntityGraph(attributePaths = "workspace")
	List<WorkspaceMembership> findAllByUserIdOrderByWorkspaceNameAsc(UUID userId);

	@EntityGraph(attributePaths = "workspace")
	Optional<WorkspaceMembership> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);
}
