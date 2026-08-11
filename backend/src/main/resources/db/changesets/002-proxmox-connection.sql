--liquibase formatted sql

--changeset david:2
CREATE TABLE proxmox_connection
(
    id               UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    base_url         VARCHAR(255) NOT NULL,
    token_id         VARCHAR(255) NOT NULL,
    token_secret_enc BYTEA        NOT NULL,
    verify_tls       BOOLEAN      NOT NULL DEFAULT true,
    created_at       TIMESTAMP    NOT NULL,
    ssh_user         VARCHAR(255),
    ssh_key_enc      BYTEA,
    ssh_ip_address   VARCHAR(45),
    ssh_port         INT CHECK ( ssh_port > 0 )
);
--rollback DROP TABLE proxmox_connection