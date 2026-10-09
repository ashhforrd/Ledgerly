create table statement_imports (
 id uuid primary key, workspace_id uuid not null references workspaces(id) on delete cascade,
 file_name varchar(255) not null, status varchar(20) not null, total_rows integer not null,
 valid_rows integer not null, duplicate_rows integer not null, created_by uuid not null references app_users(id),
 created_at timestamptz not null, confirmed_at timestamptz,
 constraint ck_statement_import_status check (status in ('PREVIEW','CONFIRMED'))
);
create table statement_import_rows (
 id uuid primary key, import_id uuid not null references statement_imports(id) on delete cascade,
 workspace_id uuid not null references workspaces(id) on delete cascade, row_number integer not null,
 occurred_on date, description varchar(200), amount numeric(19,4), type varchar(20), fingerprint varchar(64),
 validation_error varchar(500), duplicate boolean not null default false, transaction_id uuid references ledger_transactions(id),
 unique(import_id,row_number)
);
create unique index ux_import_rows_workspace_fingerprint on statement_import_rows(workspace_id,fingerprint)
 where fingerprint is not null and validation_error is null and duplicate = false;
