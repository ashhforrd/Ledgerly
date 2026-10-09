package dev.atqiya.ledgerly.invoicing.domain;
import dev.atqiya.ledgerly.identity.domain.AppUser; import jakarta.persistence.*; import java.math.BigDecimal; import java.time.*; import java.util.UUID;
@Entity @Table(name="invoice_payments") public class InvoicePayment{
 @Id private UUID id; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="invoice_id") private Invoice invoice;
 private BigDecimal amount; @Column(name="paid_on") private LocalDate paidOn; private String reference;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="recorded_by") private AppUser recordedBy; @Column(name="recorded_at") private Instant recordedAt;
 protected InvoicePayment(){} public InvoicePayment(UUID id,Invoice i,BigDecimal a,LocalDate date,String ref,AppUser actor,Instant now){this.id=id;invoice=i;amount=a;paidOn=date;reference=ref;recordedBy=actor;recordedAt=now;}
}
