package dev.atqiya.ledgerly.ledger.infrastructure;

import dev.atqiya.ledgerly.ledger.application.TransactionFilter;
import dev.atqiya.ledgerly.ledger.domain.LedgerTransaction;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public final class TransactionSpecifications {
    private TransactionSpecifications() {}
    public static Specification<LedgerTransaction> matching(UUID workspaceId, TransactionFilter filter) {
        return (root, query, cb) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            predicates.add(cb.equal(root.get("workspace").get("id"), workspaceId));
            if (filter.from() != null) predicates.add(cb.greaterThanOrEqualTo(root.get("occurredOn"), filter.from()));
            if (filter.to() != null) predicates.add(cb.lessThanOrEqualTo(root.get("occurredOn"), filter.to()));
            if (filter.type() != null) predicates.add(cb.equal(root.get("type"), filter.type()));
            if (filter.status() != null) predicates.add(cb.equal(root.get("status"), filter.status()));
            if (filter.categoryId() != null) predicates.add(cb.equal(root.get("category").get("id"), filter.categoryId()));
            if (filter.query() != null && !filter.query().isBlank()) {
                String pattern = "%" + filter.query().strip().toLowerCase(java.util.Locale.ROOT) + "%";
                predicates.add(cb.or(cb.like(cb.lower(root.get("description")), pattern),
                        cb.like(cb.lower(root.get("notes")), pattern)));
            }
            return cb.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
    }
}
