package dev.atqiya.ledgerly.workspace.infrastructure;

import dev.atqiya.ledgerly.workspace.domain.Workspace;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {

	boolean existsBySlug(String slug);
}
