package com.daf.backend.service;

import com.daf.backend.model.BackupJob;
import com.daf.backend.model.BackupRecord;
import com.daf.backend.model.BackupStatus;
import com.daf.backend.model.BackupTarget;
import com.daf.backend.repository.BackupJobRepository;
import com.daf.backend.repository.BackupRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.HexFormat;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class VerificationService {
    private final BackupRecordRepository recordRepository;
    private final TransferService transferService;
    private final BackupJobRepository jobRepository;
    private final BackupTargetService targetService;

    public BackupRecord verify(UUID recordId) throws Exception {
        BackupRecord record = recordRepository.findById(recordId).orElseThrow();

        if (record.getJob() == null)
            throw new IllegalStateException("Backup record is not connected to a job");

        BackupJob job = jobRepository.findById(record.getJob().getId()).orElseThrow();
        BackupTarget target = targetService.findById(job.getTarget().getId());

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try (InputStream in = transferService.downloadFromTarget(target, record.getRemotePath());
             DigestInputStream digestStream = new DigestInputStream(in, digest)) {
            digestStream.transferTo(OutputStream.nullOutputStream());
        }

        String actualHash = HexFormat.of().formatHex(digest.digest());
        String savedHash = record.getEncrypted() ? record.getSha256Enc() : record.getSha256Orig();
        log.info("ActualHash: {} SavedHash: {}", actualHash, savedHash);

        if (actualHash.equals(savedHash)) {
            record.setVerifiedAt(new Timestamp(System.currentTimeMillis()));
        } else {
            record.setStatus(BackupStatus.CORRUPT);
            log.warn("Backup: {} is corrupt!", record.getId());
        }

        return recordRepository.save(record);
    }
}
