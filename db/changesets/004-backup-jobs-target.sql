--liquibase formatted sql

--changeset david:4
CREATE TYPE GUEST_TYPE AS ENUM ('LXC', 'QEMU');
CREATE TYPE BACKUP_COMPRESSION AS ENUM ('ZSTD', 'GZIP', 'NONE');
CREATE TYPE DUMP_MODE AS ENUM ('SNAPSHOT', 'SUSPEND', 'STOP');
CREATE TYPE TARGET_TYPE AS ENUM ('S3', 'SFTP', 'LOCAL');

CREATE TABLE backup_targets
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(64) NOT NULL,
    type TARGET_TYPE NOT NULL,
    host VARCHAR,
    port INTEGER,
    username VARCHAR (255),
    credentials_enc BYTEA,
    base_path VARCHAR NOT NULL,
    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE backup_jobs
(
    id              UUID PRIMARY KEY            DEFAULT gen_random_uuid(),
    user_id         UUID               NOT NULL,
    name            VARCHAR(128)       NOT NULL,
    node            VARCHAR(64)        NOT NULL,
    vmid            INTEGER            NOT NULL,
    guest_type      GUEST_TYPE         NOT NULL,
    schedule_cron   VARCHAR(64)        NOT NULL,
    compression     BACKUP_COMPRESSION NOT NULL DEFAULT 'ZSTD',
    mode            DUMP_MODE          NOT NULL DEFAULT ('SNAPSHOT'),
    encrypted       BOOLEAN            NOT NULL DEFAULT true,
    retention_count INTEGER            NOT NULL DEFAULT 7,
    remove_after    BOOLEAN            NOT NULL DEFAULT true,
    target_id       UUID               NOT NULL,
    enabled         BOOLEAN            NOT NULL DEFAULT true,
    last_run_at     TIMESTAMP,
    next_run_at     TIMESTAMP,
    created_at      TIMESTAMP                   DEFAULT now(),

    CONSTRAINT fk_backup_jobs_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_backup_jobs_target FOREIGN KEY (target_id) REFERENCES backup_targets(id)
);
--rollback DROP TABLE backup_jobs; DROP TABLE backup_targets; DROP TYPE TARGET_TYPE; DROP TYPE DUMP_MODE; DROP TYPE BACKUP_COMPRESSION; DROP TYPE GUEST_TYPE;
