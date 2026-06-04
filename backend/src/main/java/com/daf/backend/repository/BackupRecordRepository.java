package com.daf.backend.repository;

import com.daf.backend.model.BackupRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BackupRecordRepository extends JpaRepository<BackupRecord, UUID> {
    Optional<BackupRecord> findByJobId(UUID jobId);
}
