package com.daf.backend.service;


import com.daf.backend.model.BackupRecord;
import com.daf.backend.repository.BackupRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BackupRecordService {
    private final BackupRecordRepository backupRecordRepository;

    public List<BackupRecord> findAll(String node, Integer vmid) {
        if (node == null || vmid == null)
            return backupRecordRepository.findAllByOrderByStartedAtDesc();

        return backupRecordRepository.findByNodeAndVmidOrderByStartedAtDesc(node, vmid);
    }

    public BackupRecord save(BackupRecord backupRecord) {
        return backupRecordRepository.save(backupRecord);
    }
}
