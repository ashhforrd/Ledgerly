package dev.atqiya.ledgerly.ledger.application;

import dev.atqiya.ledgerly.identity.domain.AppUser;
import dev.atqiya.ledgerly.ledger.domain.*;
import dev.atqiya.ledgerly.ledger.infrastructure.*;
import dev.atqiya.ledgerly.workspace.application.WorkspaceAccessService;
import dev.atqiya.ledgerly.workspace.domain.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
    private static final Set<WorkspaceRole> EDITOR_ROLES = Set.of(
            WorkspaceRole.OWNER, WorkspaceRole.ADMIN, WorkspaceRole.MEMBER);
    private final LedgerTransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final WorkspaceAccessService accessService;

    public TransactionService(LedgerTransactionRepository transactionRepository,
            CategoryRepository categoryRepository, WorkspaceAccessService accessService) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.accessService = accessService;
    }

    @Transactional
    public TransactionView create(UUID userId, UUID workspaceId, UUID categoryId,
            TransactionType type, BigDecimal amount, LocalDate occurredOn,
            String description, String notes) {
        WorkspaceMembership membership = accessService.requireRole(userId, workspaceId, EDITOR_ROLES);
        Category category = resolveCategory(workspaceId, categoryId, type);
        validate(amount, occurredOn, description);
        Instant now = Instant.now();
        LedgerTransaction transaction = new LedgerTransaction(UUID.randomUUID(), membership.getWorkspace(),
                category, type, normalizeAmount(amount), membership.getWorkspace().getDefaultCurrency(),
                occurredOn, normalizeDescription(description), normalizeNotes(notes), membership.getUser(), now);
        return TransactionView.from(transactionRepository.save(transaction));
    }

    @Transactional(readOnly = true)
    public Page<TransactionView> list(UUID userId, UUID workspaceId, TransactionFilter filter, Pageable pageable) {
        accessService.requireMembership(userId, workspaceId);
        if (filter.from() != null && filter.to() != null && filter.from().isAfter(filter.to()))
            throw new InvalidTransactionException("The from date must not be after the to date.");
        return transactionRepository.findAll(TransactionSpecifications.matching(workspaceId, filter), pageable)
                .map(TransactionView::from);
    }

    @Transactional(readOnly = true)
    public TransactionView get(UUID userId, UUID workspaceId, UUID transactionId) {
        accessService.requireMembership(userId, workspaceId);
        return TransactionView.from(requireTransaction(workspaceId, transactionId));
    }

    @Transactional
    public TransactionView update(UUID userId, UUID workspaceId, UUID transactionId,
            UUID categoryId, BigDecimal amount, LocalDate occurredOn, String description, String notes) {
        WorkspaceMembership membership = accessService.requireRole(userId, workspaceId, EDITOR_ROLES);
        LedgerTransaction transaction = requireTransaction(workspaceId, transactionId);
        Category category = resolveCategory(workspaceId, categoryId, transaction.getType());
        validate(amount, occurredOn, description);
        try {
            transaction.update(category, normalizeAmount(amount), occurredOn, normalizeDescription(description),
                    normalizeNotes(notes), membership.getUser(), Instant.now());
        } catch (IllegalStateException exception) { throw new InvalidTransactionException(exception.getMessage()); }
        return TransactionView.from(transaction);
    }

    @Transactional
    public TransactionView voidTransaction(UUID userId, UUID workspaceId, UUID transactionId, String reason) {
        WorkspaceMembership membership = accessService.requireRole(userId, workspaceId, EDITOR_ROLES);
        LedgerTransaction transaction = requireTransaction(workspaceId, transactionId);
        String normalized = reason == null ? "" : reason.strip().replaceAll("\\s+", " ");
        if (normalized.length() < 3 || normalized.length() > 300)
            throw new InvalidTransactionException("Void reason must contain between 3 and 300 characters.");
        try { transaction.voidTransaction(normalized, membership.getUser(), Instant.now()); }
        catch (IllegalStateException exception) { throw new InvalidTransactionException(exception.getMessage()); }
        return TransactionView.from(transaction);
    }

    private LedgerTransaction requireTransaction(UUID workspaceId, UUID id) {
        return transactionRepository.findByIdAndWorkspaceId(id, workspaceId)
                .orElseThrow(TransactionNotFoundException::new);
    }
    private Category resolveCategory(UUID workspaceId, UUID id, TransactionType type) {
        if (id == null) return null;
        Category category = categoryRepository.findByIdAndWorkspaceId(id, workspaceId)
                .orElseThrow(CategoryNotFoundException::new);
        if (!category.isActive()) throw new InvalidTransactionException("Archived categories cannot be assigned.");
        if (category.getType() != type) throw new InvalidTransactionException("Category type must match transaction type.");
        return category;
    }
    private void validate(BigDecimal amount, LocalDate occurredOn, String description) {
        if (amount == null || amount.signum() <= 0) throw new InvalidTransactionException("Amount must be greater than zero.");
        if (amount.scale() > 4) throw new InvalidTransactionException("Amount supports at most four decimal places.");
        if (amount.precision() - amount.scale() > 15) throw new InvalidTransactionException("Amount is too large.");
        if (occurredOn == null) throw new InvalidTransactionException("Transaction date is required.");
        if (description == null || description.strip().length() < 2 || description.strip().length() > 200)
            throw new InvalidTransactionException("Description must contain between 2 and 200 characters.");
    }
    private BigDecimal normalizeAmount(BigDecimal amount) { return amount.stripTrailingZeros(); }
    private String normalizeDescription(String value) { return value.strip().replaceAll("\\s+", " "); }
    private String normalizeNotes(String value) {
        if (value == null || value.isBlank()) return null;
        String normalized = value.strip();
        if (normalized.length() > 1000) throw new InvalidTransactionException("Notes must not exceed 1000 characters.");
        return normalized;
    }
}
