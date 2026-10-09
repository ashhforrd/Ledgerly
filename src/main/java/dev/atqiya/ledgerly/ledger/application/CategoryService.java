package dev.atqiya.ledgerly.ledger.application;

import dev.atqiya.ledgerly.identity.domain.AppUser;
import dev.atqiya.ledgerly.ledger.domain.Category;
import dev.atqiya.ledgerly.ledger.domain.TransactionType;
import dev.atqiya.ledgerly.ledger.infrastructure.CategoryRepository;
import dev.atqiya.ledgerly.workspace.application.WorkspaceAccessService;
import dev.atqiya.ledgerly.workspace.domain.Workspace;
import dev.atqiya.ledgerly.workspace.domain.WorkspaceMembership;
import dev.atqiya.ledgerly.workspace.domain.WorkspaceRole;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private static final Set<WorkspaceRole> MANAGER_ROLES = Set.of(
            WorkspaceRole.OWNER,
            WorkspaceRole.ADMIN
    );

    private final CategoryRepository categoryRepository;
    private final WorkspaceAccessService workspaceAccessService;

    public CategoryService(
            CategoryRepository categoryRepository,
            WorkspaceAccessService workspaceAccessService
    ) {
        this.categoryRepository = categoryRepository;
        this.workspaceAccessService = workspaceAccessService;
    }

    @Transactional
    public CategoryView create(
            UUID userId,
            UUID workspaceId,
            String name,
            TransactionType type
    ) {
        WorkspaceMembership membership = workspaceAccessService.requireRole(
                userId,
                workspaceId,
                MANAGER_ROLES
        );

        String normalizedName = normalizeName(name);

        if (categoryRepository.existsByWorkspace_IdAndTypeAndNameIgnoreCase(
                workspaceId,
                type,
                normalizedName
        )) {
            throw new DuplicateCategoryException();
        }

        Workspace workspace = membership.getWorkspace();
        AppUser actor = membership.getUser();
        Instant now = Instant.now();

        Category category = new Category(
                UUID.randomUUID(),
                workspace,
                normalizedName,
                type,
                actor,
                now
        );

        try {
            return CategoryView.from(categoryRepository.saveAndFlush(category));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateCategoryException();
        }
    }

    @Transactional(readOnly = true)
    public List<CategoryView> list(
            UUID userId,
            UUID workspaceId,
            TransactionType type,
            boolean includeArchived
    ) {
        workspaceAccessService.requireMembership(userId, workspaceId);

        List<Category> categories;

        if (type != null) {
            categories = includeArchived
                    ? categoryRepository
                            .findAllByWorkspace_IdOrderByTypeAscNameAsc(workspaceId)
                            .stream()
                            .filter(category -> category.getType() == type)
                            .toList()
                    : categoryRepository
                            .findAllByWorkspace_IdAndTypeAndActiveTrueOrderByNameAsc(
                                    workspaceId,
                                    type
                            );
        } else {
            categories = includeArchived
                    ? categoryRepository
                            .findAllByWorkspace_IdOrderByTypeAscNameAsc(workspaceId)
                    : categoryRepository
                            .findAllByWorkspace_IdAndActiveTrueOrderByTypeAscNameAsc(
                                    workspaceId
                            );
        }

        return categories.stream()
                .map(CategoryView::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryView get(
            UUID userId,
            UUID workspaceId,
            UUID categoryId
    ) {
        workspaceAccessService.requireMembership(userId, workspaceId);

        return CategoryView.from(
                requireCategory(workspaceId, categoryId)
        );
    }

    @Transactional
    public CategoryView rename(
            UUID userId,
            UUID workspaceId,
            UUID categoryId,
            String name
    ) {
        WorkspaceMembership membership = workspaceAccessService.requireRole(
                userId,
                workspaceId,
                MANAGER_ROLES
        );

        Category category = requireCategory(workspaceId, categoryId);
        String normalizedName = normalizeName(name);

        boolean nameChanged = !category.getName()
                .equalsIgnoreCase(normalizedName);

        if (nameChanged
                && categoryRepository
                        .existsByWorkspace_IdAndTypeAndNameIgnoreCase(
                                workspaceId,
                                category.getType(),
                                normalizedName
                        )) {
            throw new DuplicateCategoryException();
        }

        category.rename(
                normalizedName,
                membership.getUser(),
                Instant.now()
        );

        try {
            return CategoryView.from(
                    categoryRepository.saveAndFlush(category)
            );
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateCategoryException();
        }
    }

    @Transactional
    public CategoryView archive(
            UUID userId,
            UUID workspaceId,
            UUID categoryId
    ) {
        WorkspaceMembership membership = workspaceAccessService.requireRole(
                userId,
                workspaceId,
                MANAGER_ROLES
        );

        Category category = requireCategory(workspaceId, categoryId);

        if (category.isActive()) {
            category.archive(
                    membership.getUser(),
                    Instant.now()
            );
        }

        return CategoryView.from(category);
    }

    private Category requireCategory(
            UUID workspaceId,
            UUID categoryId
    ) {
        return categoryRepository
                .findByIdAndWorkspace_Id(categoryId, workspaceId)
                .orElseThrow(CategoryNotFoundException::new);
    }

    private String normalizeName(String name) {
        if (name == null) {
            throw new InvalidCategoryException(
                    "Category name is required."
            );
        }

        String normalized = name.strip()
                .replaceAll("\\s+", " ");

        if (normalized.length() < 2 || normalized.length() > 80) {
            throw new InvalidCategoryException(
                    "Category name must contain between 2 and 80 characters."
            );
        }

        return normalized;
    }
}
