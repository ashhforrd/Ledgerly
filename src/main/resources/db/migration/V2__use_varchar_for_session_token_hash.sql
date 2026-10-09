alter table auth_sessions
    alter column token_hash type varchar(64);
