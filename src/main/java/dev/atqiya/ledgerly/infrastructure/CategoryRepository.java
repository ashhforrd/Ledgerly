package dev.atqiya.ledgerly.ledger.infrastructure;

import dev.atqiya.ledgerly.ledger.domain.Category;
import dev.atqiya.ledgerly.ledger.domain.TransactionType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findAllByWorkspaceIdOrderByTypeAscNameAsc(UUID workspaceId);

    List<Category> findAllByWorkspaceIdAndActiveTrueOrderByTypeAscNameAsc(
            UUID workspaceId
    );

    List<Category> findAllByWorkspaceIdAndTypeAndActiveTrueOrderByNameAsc(
            UUID workspaceId,
            TransactionType type
    );

    Optional<Category> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

    boolean existsByWorkspaceIdAndTypeAndNameIgnoreCase(
            UUID workspaceId,
            TransactionType type,
            String name
    );
}