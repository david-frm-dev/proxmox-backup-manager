package com.daf.backend.service;

import com.daf.backend.model.BackupJob;
import com.daf.backend.model.BackupRecord;
import com.daf.backend.enums.BackupStatus;
import com.daf.backend.model.BackupTarget;
import com.daf.backend.repository.BackupRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class RetentionService {
    private final TransferService service;
    private final BackupRecordRepository repository;

    public void executeRetention(BackupJob job, BackupTarget target) throws Exception {
        List<BackupRecord> records = repository.findByJobIdAndStatusOrderByStartedAtDesc(job.getId(), BackupStatus.SUCCESS);

        if (job.getRetentionCount() == null || job.getRetentionCount() <= 0 || records.size() < job.getRetentionCount())
            return;

        List<BackupRecord> recordsForDelete = records.subList(job.getRetentionCount(), records.size());

        for (int i = 0; i < recordsForDelete.size(); i++) {
            service.deleteFromTarget(target, recordsForDelete.get(i).getRemotePath());
            service.deleteFromTarget(target, recordsForDelete.get(i).getRemotePath() + ".manifest.json");
            repository.delete(recordsForDelete.get(i));
        }
    }
}
