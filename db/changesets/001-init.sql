--liquibase formatted sql

--changeset david:1-users
CREATE TABLE users (
                       id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       username      VARCHAR(64) UNIQUE NOT NULL,
                       email         VARCHAR(255),
                       password_hash CHAR(60)    NOT NULL,                 -- BCrypt = 60 Zeichen
                       bkp_key_enc   BYTEA,                                -- gewrappter Data-Key
                       key_mode      VARCHAR(16) NOT NULL DEFAULT 'MASTER_KEY',
                       role          VARCHAR(16) NOT NULL DEFAULT 'ADMIN',
                       created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
                       last_login_at TIMESTAMPTZ
);
--rollback DROP TABLE users;