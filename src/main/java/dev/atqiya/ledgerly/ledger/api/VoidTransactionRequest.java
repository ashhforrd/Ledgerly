package dev.atqiya.ledgerly.ledger.api;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record VoidTransactionRequest(@NotBlank @Size(min = 3, max = 300) String reason) {}
