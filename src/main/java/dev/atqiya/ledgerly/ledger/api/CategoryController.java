package dev.atqiya.ledgerly.ledger.api;

import dev.atqiya.ledgerly.identity.application.AuthenticatedUser;
import dev.atqiya.ledgerly.ledger.application.CategoryService;
import dev.atqiya.ledgerly.ledger.domain.TransactionType;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID workspaceId,
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        CategoryResponse response = CategoryResponse.from(categoryService.create(
                user.id(), workspaceId, request.name(), request.type()
        ));
        return ResponseEntity.created(URI.create(
                "/api/v1/workspaces/" + workspaceId + "/categories/" + response.id()
        )).body(response);
    }

    @GetMapping
    public List<CategoryResponse> list(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID workspaceId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(defaultValue = "false") boolean includeArchived
    ) {
        return categoryService.list(user.id(), workspaceId, type, includeArchived)
                .stream().map(CategoryResponse::from).toList();
    }

    @GetMapping("/{categoryId}")
    public CategoryResponse get(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID workspaceId,
            @PathVariable UUID categoryId
    ) {
        return CategoryResponse.from(categoryService.get(user.id(), workspaceId, categoryId));
    }

    @PatchMapping("/{categoryId}")
    public CategoryResponse rename(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID workspaceId,
            @PathVariable UUID categoryId,
            @Valid @RequestBody RenameCategoryRequest request
    ) {
        return CategoryResponse.from(categoryService.rename(
                user.id(), workspaceId, categoryId, request.name()
        ));
    }

    @PostMapping("/{categoryId}/archive")
    public CategoryResponse archive(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID workspaceId,
            @PathVariable UUID categoryId
    ) {
        return CategoryResponse.from(categoryService.archive(user.id(), workspaceId, categoryId));
    }
}
