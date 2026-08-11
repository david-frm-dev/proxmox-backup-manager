--liquibase formatted sql

--changeset david:5
CREATE TYPE BACKUP_STATUS AS ENUM ('PENDING', 'RUNNING', 'SUCCESS', 'FAILED', 'CORRUPT');
CREATE TYPE BACKUP_TYPE AS ENUM ('GUEST', 'PVE_CONFIG');

CREATE TABLE backup_records
(
    id            UUID PRIMARY KEY       DEFAULT gen_random_uuid(),
    job_id        UUID REFERENCES backup_jobs (id),
    type          BACKUP_TYPE   NOT NULL,
    node          VARCHAR(64)   NOT NULL,
    vmid          INT,
    filename      VARCHAR       NOT NULL,
    remote_path   VARCHAR       NOT NULL,
    size_bytes    BIGINT,
    compression   BACKUP_COMPRESSION,
    encrypted     BOOLEAN       NOT NULL DEFAULT true,
    sha256_orig   VARCHAR(64),
    sha256_enc    VARCHAR(64),
    status        BACKUP_STATUS NOT NULL,
    error_message TEXT,
    started_at    TIMESTAMP,
    finished_at   TIMESTAMP,
    duration_ms   BIGINT,
    verified_at   TIMESTAMP
);
--rollback DROP TABLE backup_records;
