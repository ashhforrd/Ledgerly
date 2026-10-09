package dev.atqiya.ledgerly.ledger.infrastructure;

import dev.atqiya.ledgerly.ledger.domain.Category;
import dev.atqiya.ledgerly.ledger.domain.TransactionType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findAllByWorkspace_IdOrderByTypeAscNameAsc(UUID workspaceId);

    List<Category> findAllByWorkspace_IdAndActiveTrueOrderByTypeAscNameAsc(
            UUID workspaceId
    );

    List<Category> findAllByWorkspace_IdAndTypeAndActiveTrueOrderByNameAsc(
            UUID workspaceId,
            TransactionType type
    );

    Optional<Category> findByIdAndWorkspace_Id(UUID id, UUID workspaceId);

    boolean existsByWorkspace_IdAndTypeAndNameIgnoreCase(
            UUID workspaceId,
            TransactionType type,
            String name
    );
}
