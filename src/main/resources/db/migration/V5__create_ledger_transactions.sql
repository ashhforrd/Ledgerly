create table ledger_transactions (
    id uuid primary key,
    workspace_id uuid not null references workspaces(id) on delete cascade,
    category_id uuid references ledger_categories(id),
    type varchar(20) not null,
    status varchar(20) not null,
    amount numeric(19, 4) not null,
    currency varchar(3) not null,
    occurred_on date not null,
    description varchar(200) not null,
    notes varchar(1000),
    created_by uuid not null references app_users(id),
    updated_by uuid not null references app_users(id),
    voided_by uuid references app_users(id),
    voided_at timestamptz,
    void_reason varchar(300),
    created_at timestamptz not null,
    updated_at timestamptz not null,
    version bigint not null default 0,
    constraint ck_ledger_transactions_type check (type in ('INCOME', 'EXPENSE')),
    constraint ck_ledger_transactions_status check (status in ('POSTED', 'VOIDED')),
    constraint ck_ledger_transactions_amount_positive check (amount > 0),
    constraint ck_ledger_transactions_currency_uppercase check (currency = upper(currency)),
    constraint ck_ledger_transactions_description_not_blank check (length(trim(description)) > 0),
    constraint ck_ledger_transactions_void_metadata check (
        (status = 'POSTED' and voided_by is null and voided_at is null and void_reason is null)
        or
        (status = 'VOIDED' and voided_by is not null and voided_at is not null and void_reason is not null)
    )
);

create index ix_ledger_transactions_workspace_date
    on ledger_transactions(workspace_id, occurred_on desc, created_at desc);
create index ix_ledger_transactions_workspace_type
    on ledger_transactions(workspace_id, type);
create index ix_ledger_transactions_workspace_status
    on ledger_transactions(workspace_id, status);
create index ix_ledger_transactions_category on ledger_transactions(category_id);
