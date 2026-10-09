package dev.atqiya.ledgerly.invoicing.infrastructure;
import dev.atqiya.ledgerly.invoicing.domain.InvoicePayment; import java.util.UUID; import org.springframework.data.jpa.repository.JpaRepository;
public interface InvoicePaymentRepository extends JpaRepository<InvoicePayment,UUID>{}
