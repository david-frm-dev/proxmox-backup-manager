package com.daf.backend.service;

import com.daf.backend.client.ProxmoxApiClient;
import com.daf.backend.enums.RestoreMode;
import com.daf.backend.enums.RestoreStatus;
import com.daf.backend.model.BackupJob;
import com.daf.backend.model.BackupRecord;
import com.daf.backend.model.BackupTarget;
import com.daf.backend.model.RestoreJob;
import com.daf.backend.repository.BackupJobRepository;
import com.daf.backend.repository.BackupRecordRepository;
import com.daf.backend.repository.RestoreJobRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;

@Service
@AllArgsConstructor
@Slf4j
public class RestoreExecutor {
    private static final String RESTORE_STORAGE = "local";
    private static final String ROOTFS_STORAGE = "local-lvm";
    private final RestoreJobRepository restoreJobRepository;
    private final BackupRecordRepository backupRecordRepository;
    private final BackupJobRepository jobRepository;
    private final BackupTargetService targetService;
    private final TransferService transferService;
    private final EncryptionService encryptionService;
    private final ProxmoxApiClient proxmoxApiClient;
    private final JobLogService jobLogService;

    public void execute(RestoreJob restoreJob) {
        if (restoreJob == null) throw new NullPointerException("Restore job is null");

        restoreJob.setStartedAt(new Timestamp(System.currentTimeMillis()));
        restoreJob.setStatus(RestoreStatus.RUNNING);
        restoreJobRepository.save(restoreJob);

        jobLogService.publish(restoreJob.getId(), "Restore job started for LXC/VM:  " + restoreJob.getTargetVmid());
        log.info("Restore Job Started for LXC/VM: {}", restoreJob.getTargetVmid());

        try {
            BackupRecord bReckord = backupRecordRepository.findById(restoreJob.getBackupRecord().getId()).orElseThrow();
            if (bReckord.getJob() == null) throw new IllegalStateException("Job is null");

            BackupJob job = jobRepository.findById(bReckord.getJob().getId()).orElseThrow();
            BackupTarget target = targetService.findById(job.getTarget().getId());

            String archiveName = bReckord.getFilename().endsWith(".enc")
                    ? bReckord.getFilename().substring(0, bReckord.getFilename().length() - 4)
                    : bReckord.getFilename();

            Path tempFile = Files.createTempFile("pbm-restore-", ".tar.zst");

            try (InputStream in = transferService.downloadFromTarget(target, bReckord.getRemotePath())) {
                if (bReckord.getEncrypted()) {
                    encryptionService.decryptToFile(in, tempFile);
                } else {
                    Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            transferService.uploadToProxmox(tempFile, archiveName);
            Files.deleteIfExists(tempFile);

            String volid = RESTORE_STORAGE + ":backup/" + archiveName;
            boolean force = restoreJob.getMode() == RestoreMode.ORIGINAL;

            if (force) {
                String stopUpid = proxmoxApiClient.stopLxc(restoreJob.getTargetNode(), restoreJob.getTargetVmid());

                ProxmoxApiClient.TaskStatusDto stopStatus;
                do {
                    jobLogService.publish(restoreJob.getId(), "Stopping container...");
                    Thread.sleep(3000);
                    stopStatus = proxmoxApiClient.getTaskStatus(restoreJob.getTargetNode(), stopUpid);
                } while ("running".equals(stopStatus.status()));

                if (!"OK".equals(stopStatus.exitstatus())) {
                    throw new RuntimeException("Stop failed with exit status: " + stopStatus.exitstatus());
                }
            }

            String upid = proxmoxApiClient.restoreLxc(restoreJob.getTargetNode(), restoreJob.getTargetVmid(), volid, force, ROOTFS_STORAGE);

            ProxmoxApiClient.TaskStatusDto status;
            do {
                Thread.sleep(3000);
                status = proxmoxApiClient.getTaskStatus(restoreJob.getTargetNode(), upid);
                jobLogService.publish(restoreJob.getId(), "Running the restore job...");
            } while ("running".equals(status.status()));

            if (!"OK".equals(status.exitstatus())) {
                throw new RuntimeException("Restore job failed with exit status: " + status.exitstatus());
            }

            try {
                proxmoxApiClient.deleteBackup(restoreJob.getTargetNode(), RESTORE_STORAGE, volid);
            } catch (Exception e) {
                log.warn("Could not delete archive from node: {}", e.getMessage());
            }

            restoreJob.setStatus(RestoreStatus.SUCCESS);
            jobLogService.publish(restoreJob.getId(), "Restore job finished successfully");
            log.info("Restore job finished successfully");
            restoreJob.setFinishedAt(new Timestamp(System.currentTimeMillis()));
            restoreJobRepository.save(restoreJob);
        } catch (Exception e) {
            restoreJob.setStatus(RestoreStatus.FAILED);
            restoreJob.setErrorMessage(e.getMessage());
            restoreJob.setFinishedAt(new Timestamp(System.currentTimeMillis()));
            restoreJobRepository.save(restoreJob);

            if (e instanceof WebClientResponseException wcre) {
                log.error("Proxmox response body: {}", wcre.getResponseBodyAsString());
            }

            log.error("Restore Job Failed: {}", e.getMessage());
            jobLogService.publish(restoreJob.getId(), "Restore Job Failed: " + e.getMessage());
        }

    }
}
