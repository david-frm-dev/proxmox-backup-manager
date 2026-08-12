package com.daf.backend.service;

import com.daf.backend.client.ProxmoxApiClient;
import com.daf.backend.enums.BackupStatus;
import com.daf.backend.enums.BackupType;
import com.daf.backend.model.*;
import com.daf.backend.repository.BackupJobRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.HexFormat;

@Service
@AllArgsConstructor
@Slf4j
public class BackupExecutor {
    private final BackupRecordService recordService;
    private final ProxmoxApiClient apiClient;
    private final TransferService transferService;
    private final EncryptionService encryptionService;
    private final BackupTargetService backupTargetService;
    private final BackupJobRepository jobRepository;
    private final RetentionService retentionService;
    private final JobLogService jobLogService;
    /**
     * The methode execute, executes the BackupJob for Proxmox. It is the core methode for backuping everything without scheduling.
     *
     * @param job that's being executed
     * @param type defines which platform should be backuped
     */

    public void execute (BackupJob job, BackupType type) {
        if (job == null || type == null)
            throw new NullPointerException("Null is not allowed for BackupJob and BackupType");
        Timestamp now = new Timestamp(System.currentTimeMillis());

        log.info("Backup started with id: {}", job.getId());
        jobLogService.publish(job.getId(), ("Backup started for " + job.getGuestType() + " " + job.getVmid() + " on " + job.getNode()));

        BackupRecord record = new BackupRecord();
        BackupTarget target = backupTargetService.findById(job.getTarget().getId());

        job.setLastRunAt(now);
        jobRepository.save(job);

        record.setJob(job);
        record.setNode(job.getNode());
        record.setVmid(job.getVmid());
        record.setType(type);
        record.setStartedAt(now);
        record.setFilename("");
        record.setRemotePath("");
        record.setEncrypted(job.isEncrypted());
        record.setStatus(BackupStatus.PENDING);
        recordService.save(record);

        record.setStatus(BackupStatus.RUNNING);
        recordService.save(record);

        try {
            String upid = apiClient.startVzdump(job.getNode(), job.getVmid(), job.getCompression(), job.getStorage());
            ProxmoxApiClient.TaskStatusDto status;

            log.info("Vz-Dump started with id: {}", upid);
            jobLogService.publish(job.getId(), "Creating snapshot on Proxmox");

            do {
                jobLogService.publish(job.getId(), "vzdump running...");
                Thread.sleep(3000);
                status = apiClient.getTaskStatus(job.getNode(), upid);
            } while ("running".equals(status.status()));

            if (!"OK".equals(status.exitstatus())) {
                throw new RuntimeException("VZ-DUMP FAILED: " + status.exitstatus());
            }

            String volid = apiClient.findLatestVolid(job.getNode(), job.getStorage(), job.getVmid());
            String fileEnding = job.isEncrypted() ? ".enc" : "";
            record.setFilename(volid.substring(volid.lastIndexOf('/') + 1) + fileEnding);
            log.info(volid);

            String path = apiClient.getVolidPath(job.getNode(), job.getStorage(), volid);

            try (InputStream backupStream = transferService.downloadFromProxmox(path)) {
                jobLogService.publish(job.getId(), "Snapshot complete! Downloading from Proxmox");
                Path tempFile = Files.createTempFile("pbm-", job.isEncrypted() ? ".enc" : ".tmp");

                if (job.isEncrypted()) {
                    jobLogService.publish(job.getId(), "Encrypting (AES-256-GCM)");
                    String[] hashes = encryptionService.encryptToFile(backupStream, tempFile);
                    record.setSha256Orig(hashes[0]);
                    record.setSha256Enc(hashes[1]);
                } else {
                    MessageDigest shaOrig = MessageDigest.getInstance("SHA-256");
                    DigestInputStream digestStream = new DigestInputStream(backupStream, shaOrig);
                    Files.copy(digestStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
                    record.setSha256Orig(HexFormat.of().formatHex(shaOrig.digest()));
                }

                record.setSizeBytes(Files.size(tempFile));
                jobLogService.publish(job.getId(), "Uploading to target");
                record.setRemotePath(transferService.uploadToTarget(target, tempFile, record.getFilename(), job.getNode(), job.getVmid()));
                transferService.writeManifest(target, record);
                Files.deleteIfExists(tempFile);
            }

            record.setStatus(BackupStatus.SUCCESS);
            record.setFinishedAt(new Timestamp(System.currentTimeMillis()));
            record.setDurationMs(record.getFinishedAt().getTime() - record.getStartedAt().getTime());
            jobLogService.publish(job.getId(), "Backup finished successfully in " + record.getDurationMs() / 1000 + "s");

            if (job.isRemoveAfter()) {
                try {
                    jobLogService.publish(job.getId(), "Removing backup from Proxmox-Server");
                    apiClient.deleteBackup(job.getNode(), job.getStorage(), volid);
                } catch (Exception e) {
                    log.warn(e.getMessage());
                }
            }

            recordService.save(record);

            try {
                jobLogService.publish(job.getId(), "Start deleting old Backups");
                retentionService.executeRetention(job, target);
            } catch (Exception e) {
                log.warn("Retention failed: {}", e.getMessage());
            }

            jobLogService.publish(job.getId(), "Backup complete");
            log.info("Backup complete");
        } catch (Exception e) {
            record.setStatus(BackupStatus.FAILED);
            record.setErrorMessage(e.getMessage());
            record.setFinishedAt(new Timestamp(System.currentTimeMillis()));
            jobLogService.publish(job.getId(), "Backup FAILED unexpectedly!");

            recordService.save(record);
            log.error(e.getMessage());
        }
    }
}
