package dev.atqiya.ledgerly.invoicing.infrastructure;
import dev.atqiya.ledgerly.invoicing.domain.*; import java.util.*; import org.springframework.data.jpa.repository.*;
public interface InvoiceRepository extends JpaRepository<Invoice,UUID>{
 @EntityGraph(attributePaths={"customer","lines"}) List<Invoice> findAllByWorkspace_IdOrderByIssueDateDescCreatedAtDesc(UUID workspaceId);
 @EntityGraph(attributePaths={"customer","lines"}) Optional<Invoice> findByIdAndWorkspace_Id(UUID id,UUID workspaceId);
 boolean existsByWorkspace_IdAndInvoiceNumberIgnoreCase(UUID workspaceId,String number);
}
