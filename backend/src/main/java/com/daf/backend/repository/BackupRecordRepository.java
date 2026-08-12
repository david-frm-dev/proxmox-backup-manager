package com.daf.backend.repository;

import com.daf.backend.model.BackupRecord;
import com.daf.backend.enums.BackupStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BackupRecordRepository extends JpaRepository<BackupRecord, UUID> {
    List<BackupRecord> findByJobIdAndStatusOrderByStartedAtDesc(UUID job_id, BackupStatus status);
    List<BackupRecord> findAllByOrderByStartedAtDesc();
    List<BackupRecord> findByNodeAndVmidOrderByStartedAtDesc(String node, Integer vmid);
}
