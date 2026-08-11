--liquibase formatted sql

--changeset david:3

CREATE TYPE NODE_STATUS AS ENUM ('ONLINE', 'OFFLINE', 'STOPPED', 'UNKNOWN');


CREATE TABLE nodes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    status NODE_STATUS NOT NULL DEFAULT 'UNKNOWN',
    last_seen_at TIMESTAMP
);
--rollback DROP TABLE nodes; DROP TYPE NODE_STATUS