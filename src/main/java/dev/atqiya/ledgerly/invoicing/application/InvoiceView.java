package dev.atqiya.ledgerly.invoicing.application;
import dev.atqiya.ledgerly.invoicing.domain.*; import java.math.BigDecimal; import java.time.*; import java.util.*;
public record InvoiceView(UUID id,UUID customerId,String customerName,String invoiceNumber,InvoiceStatus status,String currency,LocalDate issueDate,LocalDate dueDate,String notes,BigDecimal subtotal,BigDecimal total,BigDecimal paidAmount,BigDecimal outstanding,boolean overdue,List<Line> lines,Instant createdAt,Instant updatedAt,long version){
 public record Line(int position,String description,BigDecimal quantity,BigDecimal unitPrice,BigDecimal lineTotal){}
 public static InvoiceView from(Invoice i,LocalDate today){return new InvoiceView(i.getId(),i.getCustomer().getId(),i.getCustomer().getName(),i.getInvoiceNumber(),i.getStatus(),i.getCurrency(),i.getIssueDate(),i.getDueDate(),i.getNotes(),i.getSubtotal(),i.getTotal(),i.getPaidAmount(),i.getOutstanding(),i.isOverdue(today),i.getLines().stream().map(l->new Line(l.getPosition(),l.getDescription(),l.getQuantity(),l.getUnitPrice(),l.getLineTotal())).toList(),i.getCreatedAt(),i.getUpdatedAt(),i.getVersion());}
}
