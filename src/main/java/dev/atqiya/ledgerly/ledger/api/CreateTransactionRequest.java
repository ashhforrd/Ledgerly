package dev.atqiya.ledgerly.ledger.api;
import dev.atqiya.ledgerly.ledger.domain.TransactionType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
public record CreateTransactionRequest(UUID categoryId, @NotNull TransactionType type,
        @NotNull @DecimalMin(value = "0.0001") @Digits(integer = 15, fraction = 4) BigDecimal amount,
        @NotNull LocalDate occurredOn, @NotBlank @Size(min = 2, max = 200) String description,
        @Size(max = 1000) String notes) {}
