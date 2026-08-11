--liquibase formatted sql

--changeset david:1
CREATE TABLE users
(
    id            UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
    username      VARCHAR(255) UNIQUE NOT NULL,
    email         VARCHAR(255),
    password_hash VARCHAR(60)         NOT NULL,
    bkp_key_enc   BYTEA,
    role          VARCHAR(16)         NOT NULL,
    created_at    TIMESTAMP           NOT NULL DEFAULT now(),
    last_login_at TIMESTAMP
);
--rollback DROP TABLE users;