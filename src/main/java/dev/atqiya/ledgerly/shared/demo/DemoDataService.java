package dev.atqiya.ledgerly.shared.demo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnProperty(name = "ledgerly.demo.seed-enabled", havingValue = "true")
public class DemoDataService {

    private final JdbcTemplate jdbc;

    public DemoDataService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional
    public DemoSeedResult seed(UUID userId) {
        String slug = "northstar-studio-" + userId.toString().substring(0, 8);
        List<UUID> existing = jdbc.query(
                "select id from workspaces where slug = ?",
                (result, row) -> result.getObject("id", UUID.class),
                slug
        );
        if (!existing.isEmpty()) {
            return new DemoSeedResult(existing.getFirst(), false);
        }

        UUID workspace = UUID.randomUUID();
        UUID consulting = UUID.randomUUID();
        UUID retainers = UUID.randomUUID();
        UUID software = UUID.randomUUID();
        UUID marketing = UUID.randomUUID();
        UUID operations = UUID.randomUUID();
        UUID acme = UUID.randomUUID();
        UUID meridian = UUID.randomUUID();
        Timestamp now = Timestamp.from(Instant.now());
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Jakarta"));
        LocalDate monthStart = today.withDayOfMonth(1);

        jdbc.update("""
                insert into workspaces (
                    id, name, slug, default_currency, timezone,
                    created_by, created_at, updated_at, version
                ) values (?, 'Northstar Studio', ?, 'IDR', 'Asia/Jakarta', ?, ?, ?, 0)
                """, workspace, slug, userId, now, now);
        jdbc.update("""
                insert into workspace_memberships (
                    id, workspace_id, user_id, role, joined_at
                ) values (?, ?, ?, 'OWNER', ?)
                """, UUID.randomUUID(), workspace, userId, now);

        category(consulting, workspace, "Consulting", "INCOME", userId, now);
        category(retainers, workspace, "Retainers", "INCOME", userId, now);
        category(software, workspace, "Software & tools", "EXPENSE", userId, now);
        category(marketing, workspace, "Marketing", "EXPENSE", userId, now);
        category(operations, workspace, "Operations", "EXPENSE", userId, now);

        transaction(workspace, consulting, "INCOME", "18500000", monthStart,
                "Product strategy engagement", "October milestone", userId, now);
        transaction(workspace, retainers, "INCOME", "7250000", monthStart.plusDays(3),
                "Monthly design retainer", null, userId, now);
        transaction(workspace, software, "EXPENSE", "1200000", monthStart.plusDays(1),
                "Design software licenses", null, userId, now);
        transaction(workspace, marketing, "EXPENSE", "3500000", monthStart.plusDays(4),
                "Acquisition campaign", null, userId, now);
        transaction(workspace, operations, "EXPENSE", "2100000", monthStart.plusDays(7),
                "Studio and coworking rent", null, userId, now);
        transaction(workspace, null, "EXPENSE", "750000", today,
                "AWS cloud hosting", "Ready for categorization rule demo", userId, now);

        customer(acme, workspace, "Acme Ventures", "finance@acme.example",
                "+62 21 555 0101", "Jakarta, Indonesia", userId, now);
        customer(meridian, workspace, "Meridian Labs", "billing@meridian.example",
                "+65 6555 0198", "Singapore", userId, now);

        invoice(workspace, acme, "INV-2026-001", "PAID", "12500000",
                today.minusDays(24), today.minusDays(9), "Brand strategy engagement",
                "Brand strategy engagement", userId, now, true);
        invoice(workspace, meridian, "INV-2026-002", "SENT", "8000000",
                today.minusDays(6), today.plusDays(11), "Product design retainer",
                "Monthly product design retainer", userId, now, false);
        invoice(workspace, acme, "INV-2026-003", "DRAFT", "4500000",
                today, today.plusDays(27), "Research sprint",
                "Customer research sprint", userId, now, false);

        jdbc.update("""
                insert into categorization_rules (
                    id, workspace_id, category_id, name, transaction_type,
                    description_contains, priority, active, created_by, created_at
                ) values (?, ?, ?, 'Cloud infrastructure', 'EXPENSE',
                    'cloud hosting', 10, true, ?, ?)
                """, UUID.randomUUID(), workspace, software, userId, now);

        return new DemoSeedResult(workspace, true);
    }

    private void category(
            UUID id, UUID workspace, String name, String type,
            UUID user, Timestamp now
    ) {
        jdbc.update("""
                insert into ledger_categories (
                    id, workspace_id, name, type, is_active, created_by,
                    updated_by, created_at, updated_at, version
                ) values (?, ?, ?, ?, true, ?, ?, ?, ?, 0)
                """, id, workspace, name, type, user, user, now, now);
    }

    private void transaction(
            UUID workspace, UUID category, String type, String amount,
            LocalDate date, String description, String notes, UUID user, Timestamp now
    ) {
        jdbc.update("""
                insert into ledger_transactions (
                    id, workspace_id, category_id, type, status, amount,
                    currency, occurred_on, description, notes, created_by,
                    updated_by, created_at, updated_at, version
                ) values (?, ?, ?, ?, 'POSTED', ?, 'IDR', ?, ?, ?, ?, ?, ?, ?, 0)
                """, UUID.randomUUID(), workspace, category, type,
                new BigDecimal(amount), date, description, notes, user, user, now, now);
    }

    private void customer(
            UUID id, UUID workspace, String name, String email,
            String phone, String address, UUID user, Timestamp now
    ) {
        jdbc.update("""
                insert into customers (
                    id, workspace_id, name, email, phone, billing_address,
                    active, created_by, updated_by, created_at, updated_at, version
                ) values (?, ?, ?, ?, ?, ?, true, ?, ?, ?, ?, 0)
                """, id, workspace, name, email, phone, address, user, user, now, now);
    }

    private void invoice(
            UUID workspace, UUID customer, String number, String status,
            String amount, LocalDate issueDate, LocalDate dueDate, String notes,
            String lineDescription, UUID user, Timestamp now, boolean paid
    ) {
        UUID invoice = UUID.randomUUID();
        BigDecimal total = new BigDecimal(amount);
        Timestamp sentAt = status.equals("DRAFT")
                ? null
                : Timestamp.from(now.toInstant().minusSeconds(86_400));
        Timestamp paidAt = paid
                ? Timestamp.from(now.toInstant().minusSeconds(43_200))
                : null;
        jdbc.update("""
                insert into invoices (
                    id, workspace_id, customer_id, invoice_number, status,
                    currency, issue_date, due_date, notes, subtotal, total,
                    paid_amount, sent_at, paid_at, created_by, updated_by,
                    created_at, updated_at, version
                ) values (?, ?, ?, ?, ?, 'IDR', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)
                """, invoice, workspace, customer, number, status, issueDate,
                dueDate, notes, total, total, paid ? total : BigDecimal.ZERO,
                sentAt, paidAt, user, user, now, now);
        jdbc.update("""
                insert into invoice_lines (
                    id, invoice_id, position, description,
                    quantity, unit_price, line_total
                ) values (?, ?, 1, ?, 1, ?, ?)
                """, UUID.randomUUID(), invoice, lineDescription, total, total);
        if (paid) {
            jdbc.update("""
                    insert into invoice_payments (
                        id, invoice_id, amount, paid_on, reference,
                        recorded_by, recorded_at
                    ) values (?, ?, ?, ?, 'BCA-DEMO-001', ?, ?)
                    """, UUID.randomUUID(), invoice, total, issueDate.plusDays(13), user, paidAt);
        }
    }

    public record DemoSeedResult(UUID workspaceId, boolean created) {
    }
}
