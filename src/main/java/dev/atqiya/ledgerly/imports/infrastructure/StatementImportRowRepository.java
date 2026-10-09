package dev.atqiya.ledgerly.imports.infrastructure;
import dev.atqiya.ledgerly.imports.domain.StatementImportRow; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface StatementImportRowRepository extends JpaRepository<StatementImportRow,UUID>{List<StatementImportRow> findAllByStatementImport_IdOrderByRowNumber(UUID importId);boolean existsByWorkspace_IdAndFingerprint(UUID workspaceId,String fingerprint);}
