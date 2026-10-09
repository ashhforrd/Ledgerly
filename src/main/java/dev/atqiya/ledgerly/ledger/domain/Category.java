package dev.atqiya.ledgerly.ledger.domain;

import dev.atqiya.ledgerly.identity.domain.AppUser;
import dev.atqiya.ledgerly.workspace.domain.Workspace;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ledger_categories")
public class Category {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @Column(nullable = false, length = 80)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private AppUser createdBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "updated_by", nullable = false)
    private AppUser updatedBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private long version;

    protected Category() {
    }

    public Category(
            UUID id,
            Workspace workspace,
            String name,
            TransactionType type,
            AppUser createdBy,
            Instant now
    ) {
        this.id = id;
        this.workspace = workspace;
        this.name = name;
        this.type = type;
        this.active = true;
        this.createdBy = createdBy;
        this.updatedBy = createdBy;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void rename(String name, AppUser updatedBy, Instant now) {
        this.name = name;
        this.updatedBy = updatedBy;
        this.updatedAt = now;
    }

    public void archive(AppUser updatedBy, Instant now) {
        this.active = false;
        this.updatedBy = updatedBy;
        this.updatedAt = now;
    }

    public UUID getId() {
        return id;
    }

    public UUID getWorkspaceId() {
        return workspace.getId();
    }

    public String getName() {
        return name;
    }

    public TransactionType getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    public UUID getCreatedById() {
        return createdBy.getId();
    }

    public UUID getUpdatedById() {
        return updatedBy.getId();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public long getVersion() {
        return version;
    }
}
