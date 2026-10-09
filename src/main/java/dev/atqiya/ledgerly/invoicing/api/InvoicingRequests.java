package dev.atqiya.ledgerly.invoicing.api;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.time.LocalDate; import java.util.*;
public final class InvoicingRequests{private InvoicingRequests(){}
 public record CreateCustomer(@NotBlank @Size(max=160) String name,@Email @Size(max=320) String email,@Size(max=40) String phone,@Size(max=500) String billingAddress){}
 public record Line(@NotBlank @Size(max=200) String description,@NotNull @DecimalMin("0.0001") @Digits(integer=11,fraction=4) BigDecimal quantity,@NotNull @DecimalMin("0.0") @Digits(integer=15,fraction=4) BigDecimal unitPrice){}
 public record CreateInvoice(@NotNull UUID customerId,@NotBlank @Size(max=60) String invoiceNumber,@NotNull LocalDate issueDate,@NotNull LocalDate dueDate,@Size(max=1000) String notes,@NotEmpty List<@Valid Line> lines){}
 public record Payment(@NotNull @DecimalMin("0.0001") @Digits(integer=15,fraction=4) BigDecimal amount,@NotNull LocalDate paidOn,@Size(max=120) String reference){}
}
