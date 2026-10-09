package dev.atqiya.ledgerly.ledger.api;

import dev.atqiya.ledgerly.identity.application.AuthenticatedUser;
import dev.atqiya.ledgerly.ledger.application.*;
import dev.atqiya.ledgerly.ledger.domain.*;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/transactions")
public class TransactionController {
    private final TransactionService service;
    public TransactionController(TransactionService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID workspaceId, @Valid @RequestBody CreateTransactionRequest request) {
        TransactionResponse response = TransactionResponse.from(service.create(user.id(), workspaceId,
                request.categoryId(), request.type(), request.amount(), request.occurredOn(),
                request.description(), request.notes()));
        return ResponseEntity.created(URI.create("/api/v1/workspaces/" + workspaceId
                + "/transactions/" + response.id())).body(response);
    }

    @GetMapping
    public PageResponse<TransactionResponse> list(@AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID workspaceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) TransactionStatus status,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (page < 0 || size < 1 || size > 100)
            throw new InvalidTransactionException("Page must be non-negative and size must be between 1 and 100.");
        var result = service.list(user.id(), workspaceId,
                new TransactionFilter(from, to, type, status, categoryId, query),
                PageRequest.of(page, size, Sort.by(Sort.Order.desc("occurredOn"), Sort.Order.desc("createdAt"))));
        return PageResponse.from(result, TransactionResponse::from);
    }

    @GetMapping("/{transactionId}")
    public TransactionResponse get(@AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID workspaceId, @PathVariable UUID transactionId) {
        return TransactionResponse.from(service.get(user.id(), workspaceId, transactionId));
    }

    @PutMapping("/{transactionId}")
    public TransactionResponse update(@AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID workspaceId, @PathVariable UUID transactionId,
            @Valid @RequestBody UpdateTransactionRequest request) {
        return TransactionResponse.from(service.update(user.id(), workspaceId, transactionId,
                request.categoryId(), request.amount(), request.occurredOn(), request.description(), request.notes()));
    }

    @PostMapping("/{transactionId}/void")
    public TransactionResponse voidTransaction(@AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID workspaceId, @PathVariable UUID transactionId,
            @Valid @RequestBody VoidTransactionRequest request) {
        return TransactionResponse.from(service.voidTransaction(user.id(), workspaceId, transactionId, request.reason()));
    }
}
