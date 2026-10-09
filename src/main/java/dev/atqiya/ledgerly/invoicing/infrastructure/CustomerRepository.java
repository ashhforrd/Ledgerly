package dev.atqiya.ledgerly.invoicing.infrastructure;
import dev.atqiya.ledgerly.invoicing.domain.Customer; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface CustomerRepository extends JpaRepository<Customer,UUID>{List<Customer> findAllByWorkspace_IdAndActiveTrueOrderByName(UUID workspaceId);Optional<Customer> findByIdAndWorkspace_Id(UUID id,UUID workspaceId);}
