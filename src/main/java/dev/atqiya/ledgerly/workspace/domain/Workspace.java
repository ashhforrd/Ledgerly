package dev.atqiya.ledgerly.workspace.domain;

import dev.atqiya.ledgerly.identity.domain.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "workspaces")
public class Workspace {

	@Id
	private UUID id;

	@Column(nullable = false, length = 120)
	private String name;

	@Column(nullable = false, unique = true, length = 80)
	private String slug;

	@Column(name = "default_currency", nullable = false, length = 3)
	private String defaultCurrency;

	@Column(nullable = false, length = 80)
	private String timezone;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "created_by", nullable = false)
	private AppUser createdBy;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@Version
	private long version;

	protected Workspace() {
	}

	public Workspace(
			UUID id,
			String name,
			String slug,
			String defaultCurrency,
			String timezone,
			AppUser createdBy,
			Instant now) {
		this.id = id;
		this.name = name;
		this.slug = slug;
		this.defaultCurrency = defaultCurrency;
		this.timezone = timezone;
		this.createdBy = createdBy;
		this.createdAt = now;
		this.updatedAt = now;
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSlug() {
		return slug;
	}

	public String getDefaultCurrency() {
		return defaultCurrency;
	}

	public String getTimezone() {
		return timezone;
	}
}
