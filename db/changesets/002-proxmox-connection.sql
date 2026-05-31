--liquibase formatted sql

--changeset david:2
CREATE TABLE proxmox_connection
(
    id               UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    base_url         VARCHAR(255) NOT NULL,
    token_id         VARCHAR(255) NOT NULL,
    token_secret_enc BYTEA        NOT NULL,
    verify_tls       BOOLEAN      NOT NULL DEFAULT true,
    created_at       TIMESTAMP    NOT NULL
);
--rollback DROP TABLE proxmox_connection