create table ledger_categories (
    id uuid primary key,
    workspace_id uuid not null
        references workspaces(id) on delete cascade,
    name varchar(80) not null,
    type varchar(20) not null,
    is_active boolean not null default true,
    created_by uuid not null
        references app_users(id),
    updated_by uuid not null
        references app_users(id),
    created_at timestamptz not null,
    updated_at timestamptz not null,
    version bigint not null default 0,

    constraint ck_ledger_categories_type
        check (type in ('INCOME', 'EXPENSE')),

    constraint ck_ledger_categories_name_not_blank
        check (length(trim(name)) > 0)
);

create unique index ux_ledger_categories_workspace_type_name
    on ledger_categories (
        workspace_id,
        type,
        lower(name)
    );

create index ix_ledger_categories_workspace_active
    on ledger_categories (
        workspace_id,
        is_active
    );

create index ix_ledger_categories_workspace_type
    on ledger_categories (
        workspace_id,
        type
    );