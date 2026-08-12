package com.daf.backend.service;

import com.daf.backend.dto.RestoreRequestDto;
import com.daf.backend.enums.BackupStatus;
import com.daf.backend.enums.RestoreStatus;
import com.daf.backend.model.BackupRecord;
import com.daf.backend.model.RestoreJob;
import com.daf.backend.repository.BackupRecordRepository;
import com.daf.backend.repository.RestoreJobRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class RestoreService {
    private final RestoreJobRepository jobRepository;
    private final BackupRecordRepository recordRepository;
    private final RestoreQueueService queueService;

    public RestoreJob startRestore(UUID recordId, RestoreRequestDto dto) {
        BackupRecord record = recordRepository.findById(recordId).orElseThrow();

        if (record.getStatus() != BackupStatus.SUCCESS) throw new IllegalStateException("Record wasn't successful");

        RestoreJob job = new RestoreJob();
        job.setBackupRecord(record);
        job.setTargetNode(dto.getTargetNode());
        job.setTargetVmid(dto.getTargetVmid());
        job.setMode(dto.getRestoreMode());
        job.setStatus(RestoreStatus.PENDING);
        job = jobRepository.save(job);

        queueService.enqueue(job.getId());
        return job;
    }
}
