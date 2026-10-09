create table customers (
    id uuid primary key,
    workspace_id uuid not null references workspaces(id) on delete cascade,
    name varchar(160) not null,
    email varchar(320),
    phone varchar(40),
    billing_address varchar(500),
    active boolean not null default true,
    created_by uuid not null references app_users(id),
    updated_by uuid not null references app_users(id),
    created_at timestamptz not null,
    updated_at timestamptz not null,
    version bigint not null default 0,
    constraint ck_customers_name_not_blank check (length(trim(name)) > 0)
);
create index ix_customers_workspace_name on customers(workspace_id, lower(name));

create table invoices (
    id uuid primary key,
    workspace_id uuid not null references workspaces(id) on delete cascade,
    customer_id uuid not null references customers(id),
    invoice_number varchar(60) not null,
    status varchar(20) not null,
    currency varchar(3) not null,
    issue_date date not null,
    due_date date not null,
    notes varchar(1000),
    subtotal numeric(19,4) not null,
    total numeric(19,4) not null,
    paid_amount numeric(19,4) not null default 0,
    sent_at timestamptz,
    paid_at timestamptz,
    voided_at timestamptz,
    created_by uuid not null references app_users(id),
    updated_by uuid not null references app_users(id),
    created_at timestamptz not null,
    updated_at timestamptz not null,
    version bigint not null default 0,
    constraint ux_invoices_workspace_number unique(workspace_id, invoice_number),
    constraint ck_invoices_status check (status in ('DRAFT','SENT','PAID','VOIDED')),
    constraint ck_invoices_dates check (due_date >= issue_date),
    constraint ck_invoices_amounts check (subtotal >= 0 and total >= 0 and paid_amount >= 0 and paid_amount <= total)
);
create index ix_invoices_workspace_due on invoices(workspace_id, due_date);
create index ix_invoices_workspace_status on invoices(workspace_id, status);

create table invoice_lines (
    id uuid primary key,
    invoice_id uuid not null references invoices(id) on delete cascade,
    position integer not null,
    description varchar(200) not null,
    quantity numeric(15,4) not null,
    unit_price numeric(19,4) not null,
    line_total numeric(19,4) not null,
    unique(invoice_id, position),
    constraint ck_invoice_lines_values check (quantity > 0 and unit_price >= 0 and line_total >= 0)
);

create table invoice_payments (
    id uuid primary key,
    invoice_id uuid not null references invoices(id),
    amount numeric(19,4) not null,
    paid_on date not null,
    reference varchar(120),
    recorded_by uuid not null references app_users(id),
    recorded_at timestamptz not null,
    constraint ck_invoice_payments_amount check (amount > 0)
);
create index ix_invoice_payments_invoice on invoice_payments(invoice_id, paid_on);
