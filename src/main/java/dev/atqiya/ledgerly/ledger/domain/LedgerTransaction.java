package dev.atqiya.ledgerly.ledger.domain;

import dev.atqiya.ledgerly.identity.domain.AppUser;
import dev.atqiya.ledgerly.workspace.domain.Workspace;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "ledger_transactions")
public class LedgerTransaction {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id", nullable = false) private Workspace workspace;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id") private Category category;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private TransactionType type;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private TransactionStatus status;
    @Column(nullable = false, precision = 19, scale = 4) private BigDecimal amount;
    @Column(nullable = false, length = 3) private String currency;
    @Column(name = "occurred_on", nullable = false) private LocalDate occurredOn;
    @Column(nullable = false, length = 200) private String description;
    @Column(length = 1000) private String notes;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "created_by") private AppUser createdBy;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "updated_by") private AppUser updatedBy;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "voided_by") private AppUser voidedBy;
    @Column(name = "voided_at") private Instant voidedAt;
    @Column(name = "void_reason", length = 300) private String voidReason;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    @Version private long version;

    protected LedgerTransaction() {}

    public LedgerTransaction(UUID id, Workspace workspace, Category category,
            TransactionType type, BigDecimal amount, String currency, LocalDate occurredOn,
            String description, String notes, AppUser actor, Instant now) {
        this.id = id; this.workspace = workspace; this.category = category; this.type = type;
        this.status = TransactionStatus.POSTED; this.amount = amount; this.currency = currency;
        this.occurredOn = occurredOn; this.description = description; this.notes = notes;
        this.createdBy = actor; this.updatedBy = actor; this.createdAt = now; this.updatedAt = now;
    }

    public void update(Category category, BigDecimal amount, LocalDate occurredOn,
            String description, String notes, AppUser actor, Instant now) {
        requirePosted();
        this.category = category; this.amount = amount; this.occurredOn = occurredOn;
        this.description = description; this.notes = notes; this.updatedBy = actor; this.updatedAt = now;
    }

    public void voidTransaction(String reason, AppUser actor, Instant now) {
        requirePosted();
        this.status = TransactionStatus.VOIDED; this.voidReason = reason;
        this.voidedBy = actor; this.voidedAt = now; this.updatedBy = actor; this.updatedAt = now;
    }

    private void requirePosted() {
        if (status != TransactionStatus.POSTED) throw new IllegalStateException("Voided transactions cannot be changed.");
    }

    public UUID getId() { return id; }
    public UUID getWorkspaceId() { return workspace.getId(); }
    public UUID getCategoryId() { return category == null ? null : category.getId(); }
    public String getCategoryName() { return category == null ? null : category.getName(); }
    public TransactionType getType() { return type; }
    public TransactionStatus getStatus() { return status; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public LocalDate getOccurredOn() { return occurredOn; }
    public String getDescription() { return description; }
    public String getNotes() { return notes; }
    public UUID getCreatedById() { return createdBy.getId(); }
    public UUID getUpdatedById() { return updatedBy.getId(); }
    public UUID getVoidedById() { return voidedBy == null ? null : voidedBy.getId(); }
    public Instant getVoidedAt() { return voidedAt; }
    public String getVoidReason() { return voidReason; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public long getVersion() { return version; }
}
