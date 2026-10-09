create table categorization_rules (
    id uuid primary key,
    workspace_id uuid not null references workspaces(id) on delete cascade,
    category_id uuid not null references ledger_categories(id),
    name varchar(120) not null,
    transaction_type varchar(20) not null,
    description_contains varchar(120) not null,
    priority integer not null,
    active boolean not null default true,
    created_by uuid not null references app_users(id),
    created_at timestamptz not null,
    constraint ck_categorization_rule_type check (transaction_type in ('INCOME','EXPENSE')),
    constraint ck_categorization_rule_priority check (priority between 1 and 1000),
    constraint ux_categorization_rule_priority unique(workspace_id, priority)
);
create index ix_categorization_rules_matching
    on categorization_rules(workspace_id, active, transaction_type, priority);
