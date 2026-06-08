package com.daf.backend.service;

import com.daf.backend.client.ProxmoxApiClient;
import com.daf.backend.model.BackupJob;
import com.daf.backend.model.BackupRecord;
import com.daf.backend.model.BackupStatus;
import com.daf.backend.model.BackupType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.sql.Timestamp;

@Service
@AllArgsConstructor
@Slf4j
public class BackupExecutor {
    private final BackupRecordService recordService;
    private final ProxmoxApiClient apiClient;
    private final TransferService transferService;

    public void execute (BackupJob job, BackupType type) {
        BackupRecord record = new BackupRecord();


        record.setJob(job);
        record.setNode(job.getNode());
        record.setVmid(job.getVmid());
        record.setType(type);
        record.setStartedAt(new Timestamp(System.currentTimeMillis()));
        record.setFilename("");
        record.setRemotePath("");
        record.setEncrypted(job.isEncrypted());
        record.setStatus(BackupStatus.PENDING);
        recordService.save(record);

        record.setStatus(BackupStatus.RUNNING);
        recordService.save(record);

        try {
            // vzdump starten → kommt später
            // download → encrypt → upload → kommt später
            String upid = apiClient.startVzdump(job.getNode(), job.getVmid(), job.getCompression());
            ProxmoxApiClient.TaskStatusDto status;

            log.info("VZDUMP STARTED WITH: {}", upid);

            do {
                Thread.sleep(3000);
                status = apiClient.getTaskStatus(job.getNode(), upid);
            } while ("running".equals(status.status()));

            if (!"OK".equals(status.exitstatus())) {
                throw new RuntimeException("vzdump fehlgeschlagen: " + status.exitstatus());
            }

            //TODO: STORAGE INTEGRATION WITH NOT LOCAL
            String volid = apiClient.findLatestVolid(job.getNode(), "local", job.getVmid());
            record.setFilename(volid.substring(volid.lastIndexOf('/') + 1));
            log.info(volid);

            String path = apiClient.getVolidPath(job.getNode(), "local", volid);

            try (InputStream backupStream = transferService.downloadFromProxmox(path)) {
                // encrypt + upload kommt hier

            }

            record.setStatus(BackupStatus.SUCCESS);
            record.setFinishedAt(new Timestamp(System.currentTimeMillis()));
            record.setDurationMs(record.getFinishedAt().getTime() - record.getStartedAt().getTime());

            recordService.save(record);
            log.info("Backup finished");
        } catch (Exception e) {
            record.setStatus(BackupStatus.FAILED);
            record.setErrorMessage(e.getMessage());
            record.setFinishedAt(new Timestamp(System.currentTimeMillis()));

            recordService.save(record);
            log.error(e.getMessage());
        }
    }
}
