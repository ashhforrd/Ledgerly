create table app_users (
    id uuid primary key,
    email varchar(320) not null,
    display_name varchar(120) not null,
    password_hash varchar(100) not null,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    version bigint not null default 0
);

create unique index ux_app_users_email_lower on app_users (lower(email));

create table workspaces (
    id uuid primary key,
    name varchar(120) not null,
    slug varchar(80) not null,
    default_currency char(3) not null,
    timezone varchar(80) not null,
    created_by uuid not null references app_users(id),
    created_at timestamptz not null,
    updated_at timestamptz not null,
    version bigint not null default 0,
    constraint ck_workspaces_currency_uppercase
        check (default_currency = upper(default_currency))
);

create unique index ux_workspaces_slug_lower on workspaces (lower(slug));

create table workspace_memberships (
    id uuid primary key,
    workspace_id uuid not null references workspaces(id) on delete cascade,
    user_id uuid not null references app_users(id) on delete cascade,
    role varchar(20) not null,
    joined_at timestamptz not null,
    constraint ux_workspace_memberships_workspace_user unique (workspace_id, user_id),
    constraint ck_workspace_memberships_role
        check (role in ('OWNER', 'ADMIN', 'MEMBER', 'VIEWER'))
);

create index ix_workspace_memberships_user on workspace_memberships(user_id);

create table auth_sessions (
    id uuid primary key,
    user_id uuid not null references app_users(id) on delete cascade,
    token_hash char(64) not null,
    created_at timestamptz not null,
    expires_at timestamptz not null,
    revoked_at timestamptz,
    constraint ux_auth_sessions_token_hash unique (token_hash),
    constraint ck_auth_sessions_expiry check (expires_at > created_at)
);

create index ix_auth_sessions_user on auth_sessions(user_id);
create index ix_auth_sessions_active on auth_sessions(token_hash, expires_at)
    where revoked_at is null;
