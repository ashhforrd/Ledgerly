package dev.atqiya.ledgerly.invoicing.domain;

import dev.atqiya.ledgerly.identity.domain.AppUser; import dev.atqiya.ledgerly.workspace.domain.Workspace;
import jakarta.persistence.*; import java.math.*; import java.time.*; import java.util.*;

@Entity @Table(name="invoices")
public class Invoice {
 @Id private UUID id; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="workspace_id") private Workspace workspace;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="customer_id") private Customer customer;
 @Column(name="invoice_number") private String invoiceNumber; @Enumerated(EnumType.STRING) private InvoiceStatus status;
 private String currency; @Column(name="issue_date") private LocalDate issueDate; @Column(name="due_date") private LocalDate dueDate; private String notes;
 private BigDecimal subtotal; private BigDecimal total; @Column(name="paid_amount") private BigDecimal paidAmount;
 @Column(name="sent_at") private Instant sentAt; @Column(name="paid_at") private Instant paidAt; @Column(name="voided_at") private Instant voidedAt;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="created_by") private AppUser createdBy;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="updated_by") private AppUser updatedBy;
 @Column(name="created_at") private Instant createdAt; @Column(name="updated_at") private Instant updatedAt; @Version private long version;
 @OneToMany(mappedBy="invoice",cascade=CascadeType.ALL,orphanRemoval=true) @OrderBy("position asc") private List<InvoiceLine> lines=new ArrayList<>();
 protected Invoice(){}
 public Invoice(UUID id,Workspace w,Customer c,String number,LocalDate issue,LocalDate due,String notes,AppUser actor,Instant now){this.id=id;workspace=w;customer=c;invoiceNumber=number;issueDate=issue;dueDate=due;this.notes=notes;createdBy=actor;updatedBy=actor;createdAt=now;updatedAt=now;currency=w.getDefaultCurrency();status=InvoiceStatus.DRAFT;subtotal=BigDecimal.ZERO.setScale(4);total=subtotal;paidAmount=subtotal;}
 public void addLine(String description,BigDecimal quantity,BigDecimal unitPrice){if(status!=InvoiceStatus.DRAFT)throw new IllegalStateException("Only draft invoices can be edited.");lines.add(new InvoiceLine(UUID.randomUUID(),this,lines.size()+1,description,quantity,unitPrice));recalculate();}
 private void recalculate(){subtotal=lines.stream().map(InvoiceLine::getLineTotal).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(4,RoundingMode.HALF_UP);total=subtotal;}
 public void send(AppUser actor,Instant now){if(status!=InvoiceStatus.DRAFT||lines.isEmpty())throw new IllegalStateException("Only non-empty draft invoices can be sent.");status=InvoiceStatus.SENT;sentAt=now;updatedBy=actor;updatedAt=now;}
 public void recordPayment(BigDecimal amount,AppUser actor,Instant now){if(status!=InvoiceStatus.SENT)throw new IllegalStateException("Payments require a sent invoice.");if(paidAmount.add(amount).compareTo(total)>0)throw new IllegalStateException("Payment exceeds outstanding balance.");paidAmount=paidAmount.add(amount);updatedBy=actor;updatedAt=now;if(paidAmount.compareTo(total)==0){status=InvoiceStatus.PAID;paidAt=now;}}
 public void voidInvoice(AppUser actor,Instant now){if(status==InvoiceStatus.PAID||status==InvoiceStatus.VOIDED)throw new IllegalStateException("Paid or voided invoices cannot be voided.");status=InvoiceStatus.VOIDED;voidedAt=now;updatedBy=actor;updatedAt=now;}
 public boolean isOverdue(LocalDate today){return status==InvoiceStatus.SENT&&dueDate.isBefore(today)&&getOutstanding().signum()>0;}
 public BigDecimal getOutstanding(){return total.subtract(paidAmount);}
 public UUID getId(){return id;} public UUID getWorkspaceId(){return workspace.getId();} public Customer getCustomer(){return customer;} public String getInvoiceNumber(){return invoiceNumber;} public InvoiceStatus getStatus(){return status;} public String getCurrency(){return currency;} public LocalDate getIssueDate(){return issueDate;} public LocalDate getDueDate(){return dueDate;} public String getNotes(){return notes;} public BigDecimal getSubtotal(){return subtotal;} public BigDecimal getTotal(){return total;} public BigDecimal getPaidAmount(){return paidAmount;} public List<InvoiceLine> getLines(){return List.copyOf(lines);} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;} public long getVersion(){return version;}
}
