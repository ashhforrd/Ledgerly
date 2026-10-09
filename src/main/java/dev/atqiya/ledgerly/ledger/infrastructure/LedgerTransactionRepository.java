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
    Optional<LedgerTransaction> findByIdAndWorkspace_Id(UUID id, UUID workspaceId);

    @EntityGraph(attributePaths = "category")
    java.util.List<LedgerTransaction> findAllByWorkspace_IdAndStatusAndOccurredOnBetweenOrderByOccurredOnAsc(
            UUID workspaceId,
            dev.atqiya.ledgerly.ledger.domain.TransactionStatus status,
            java.time.LocalDate from,
            java.time.LocalDate to
    );

    java.util.List<LedgerTransaction> findAllByWorkspace_IdAndStatusAndCategoryIsNull(
            UUID workspaceId,
            dev.atqiya.ledgerly.ledger.domain.TransactionStatus status
    );
}
