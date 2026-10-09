package dev.atqiya.ledgerly.workspace.application;

import dev.atqiya.ledgerly.identity.domain.AppUser;
import dev.atqiya.ledgerly.identity.infrastructure.AppUserRepository;
import dev.atqiya.ledgerly.workspace.domain.Workspace;
import dev.atqiya.ledgerly.workspace.domain.WorkspaceMembership;
import dev.atqiya.ledgerly.workspace.domain.WorkspaceRole;
import dev.atqiya.ledgerly.workspace.infrastructure.WorkspaceMembershipRepository;
import dev.atqiya.ledgerly.workspace.infrastructure.WorkspaceRepository;
import java.text.Normalizer;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkspaceService {

    private final AppUserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMembershipRepository membershipRepository;
    private final WorkspaceAccessService accessService;

    public WorkspaceService(
            AppUserRepository userRepository,
            WorkspaceRepository workspaceRepository,
            WorkspaceMembershipRepository membershipRepository,
            WorkspaceAccessService accessService
    ) {
        this.userRepository = userRepository;
        this.workspaceRepository = workspaceRepository;
        this.membershipRepository = membershipRepository;
        this.accessService = accessService;
    }

    @Transactional
    public WorkspaceView create(
            UUID userId,
            String name,
            String defaultCurrency,
            String timezone
    ) {
        AppUser owner = userRepository.findById(userId).orElseThrow();
        String normalizedName = name.strip();
        String normalizedCurrency = defaultCurrency.toUpperCase(Locale.ROOT);
        String normalizedTimezone = validateTimezone(timezone);
        Instant now = Instant.now();

        Workspace workspace = new Workspace(
                UUID.randomUUID(),
                normalizedName,
                createUniqueSlug(normalizedName),
                normalizedCurrency,
                normalizedTimezone,
                owner,
                now
        );
        workspaceRepository.save(workspace);

        WorkspaceMembership membership = new WorkspaceMembership(
                UUID.randomUUID(),
                workspace,
                owner,
                WorkspaceRole.OWNER,
                now
        );
        membershipRepository.save(membership);

        return WorkspaceView.from(workspace, WorkspaceRole.OWNER);
    }

    @Transactional(readOnly = true)
    public List<WorkspaceView> list(UUID userId) {
        return membershipRepository
                .findAllByUserIdOrderByWorkspaceNameAsc(userId)
                .stream()
                .map(membership -> WorkspaceView.from(
                        membership.getWorkspace(),
                        membership.getRole()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public WorkspaceView get(UUID userId, UUID workspaceId) {
        WorkspaceMembership membership = accessService.requireMembership(
                userId,
                workspaceId
        );
        return WorkspaceView.from(
                membership.getWorkspace(),
                membership.getRole()
        );
    }

    private String validateTimezone(String timezone) {
        String normalized = timezone.strip();
        try {
            return ZoneId.of(normalized).getId();
        } catch (DateTimeException exception) {
            throw new InvalidWorkspaceException(
                    "Timezone must be a valid IANA timezone."
            );
        }
    }

    private String createUniqueSlug(String name) {
        String base = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        if (base.isBlank()) {
            base = "workspace";
        }
        base = base.substring(0, Math.min(base.length(), 60));

        if (!workspaceRepository.existsBySlug(base)) {
            return base;
        }
        return base + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
