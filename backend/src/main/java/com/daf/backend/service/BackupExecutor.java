package com.daf.backend.service;

import com.daf.backend.client.ProxmoxApiClient;
import com.daf.backend.model.*;
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

    /**
     * The methode execute, executes the BackupJob for Proxmox. It is the core methode for backuping everything without scheduling.
     *
     * @param job that's being executed
     * @param type defines which platform should be backuped
     */

    public void execute (BackupJob job, BackupType type) {
        BackupRecord record = new BackupRecord();
        BackupTarget target = backupTargetService.findById(job.getTarget().getId());

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
            String fileEnding = job.isEncrypted() ? ".enc" : "";
            record.setFilename(volid.substring(volid.lastIndexOf('/') + 1) + fileEnding);
            log.info(volid);

            String path = apiClient.getVolidPath(job.getNode(), "local", volid);

            try (InputStream backupStream = transferService.downloadFromProxmox(path)) {
                Path tempFile = Files.createTempFile("pbm-", job.isEncrypted() ? ".enc" : ".tmp");

                if (job.isEncrypted()) {
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
                record.setRemotePath(transferService.uploadToTarget(target, tempFile, record.getFilename()));
                Files.deleteIfExists(tempFile);
            }

            record.setStatus(BackupStatus.SUCCESS);
            record.setFinishedAt(new Timestamp(System.currentTimeMillis()));
            record.setDurationMs(record.getFinishedAt().getTime() - record.getStartedAt().getTime());

            if (job.isRemoveAfter()) {
                try {
                    apiClient.deleteBackup(job.getNode(), "local", volid);
                } catch (Exception e) {
                    log.warn(e.getMessage());
                }
            }

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
