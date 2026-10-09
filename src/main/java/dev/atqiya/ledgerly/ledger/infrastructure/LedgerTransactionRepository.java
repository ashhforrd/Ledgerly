package dev.atqiya.ledgerly.ledger.infrastructure;

import dev.atqiya.ledgerly.ledger.domain.LedgerTransaction;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LedgerTransactionRepository extends JpaRepository<LedgerTransaction, UUID>,
        JpaSpecificationExecutor<LedgerTransaction> {
    @EntityGraph(attributePaths = "category")
    Optional<LedgerTransaction> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
}
