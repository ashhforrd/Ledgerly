package dev.atqiya.ledgerly.imports.infrastructure;
import dev.atqiya.ledgerly.imports.domain.StatementImport; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface StatementImportRepository extends JpaRepository<StatementImport,UUID>{Optional<StatementImport> findByIdAndWorkspace_Id(UUID id,UUID workspaceId);}
