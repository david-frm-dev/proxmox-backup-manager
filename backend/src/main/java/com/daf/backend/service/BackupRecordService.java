package com.daf.backend.service;


import com.daf.backend.model.BackupRecord;
import com.daf.backend.repository.BackupRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class BackupRecordService {
    private final BackupRecordRepository backupRecordRepository;

    public BackupRecord findByJobId(UUID jobId) {
        return backupRecordRepository.findByJobId(jobId).orElse(null);
    }

    public BackupRecord save(BackupRecord backupRecord) {
        return backupRecordRepository.save(backupRecord);
    }
}
