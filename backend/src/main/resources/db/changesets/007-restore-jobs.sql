--liquibase formatted sql

--changeset david:7
CREATE TYPE RESTORE_MODE AS ENUM ('ORIGINAL', 'NEW_LXC', 'OTHER_NODE');
CREATE TYPE RESTORE_STATUS AS ENUM ('PENDING', 'RUNNING', 'SUCCESS', 'FAILED');

CREATE TABLE restore_jobs(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    backup_record_id UUID NOT NULL,
    target_node VARCHAR(64) NOT NULL,
    target_vmid INT NOT NULL,
    mode RESTORE_MODE NOT NULL,
    status RESTORE_STATUS NOT NULL,
    error_message TEXT,
    started_at TIMESTAMP,
    finished_at TIMESTAMP,

    CONSTRAINT fk_restore_jobs_record FOREIGN KEY (backup_record_id) REFERENCES backup_records(id)
);
--rollback DROP TABLE restore_jobs;