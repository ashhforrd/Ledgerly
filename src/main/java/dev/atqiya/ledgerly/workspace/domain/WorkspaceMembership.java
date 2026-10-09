package dev.atqiya.ledgerly.workspace.domain;

import dev.atqiya.ledgerly.identity.domain.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "workspace_memberships")
public class WorkspaceMembership {

	@Id
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "workspace_id", nullable = false)
	private Workspace workspace;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private AppUser user;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private WorkspaceRole role;

	@Column(name = "joined_at", nullable = false)
	private Instant joinedAt;

	protected WorkspaceMembership() {
	}

	public WorkspaceMembership(
			UUID id, Workspace workspace, AppUser user, WorkspaceRole role, Instant joinedAt) {
		this.id = id;
		this.workspace = workspace;
		this.user = user;
		this.role = role;
		this.joinedAt = joinedAt;
	}

	public Workspace getWorkspace() {
		return workspace;
	}

	public UUID getId() {
		return id;
	}

	public AppUser getUser() {
		return user;
	}

	public WorkspaceRole getRole() {
		return role;
	}

	public Instant getJoinedAt() {
		return joinedAt;
	}
}
