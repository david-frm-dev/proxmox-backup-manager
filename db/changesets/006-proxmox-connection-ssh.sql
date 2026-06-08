--liquibase formatted sql

--changeset david:6
ALTER TABLE proxmox_connection
ADD ssh_user VARCHAR(255);

ALTER TABLE proxmox_connection
ADD ssh_key_enc BYTEA;
--rollback ALTER TABLE proxmox_connection DROP ssh_user; ALTER TABLE proxmox_connection DROP ssh_key_enc