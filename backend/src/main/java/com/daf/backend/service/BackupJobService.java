package com.daf.backend.service;

import com.daf.backend.model.BackupJob;
import com.daf.backend.model.BackupJobDto;
import com.daf.backend.model.BackupTarget;
import com.daf.backend.model.BackupTargetDto;
import com.daf.backend.repository.BackupJobRepository;
import com.daf.backend.repository.BackupTargetRepository;
import com.daf.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BackupJobService {
    private final BackupJobRepository backupJobRepository;
    private final UserRepository userRepository;
    private final BackupTargetRepository backupTargetRepository;

    public List<BackupJob> findAll() {
        return backupJobRepository.findAll();
    }

    public BackupJob findById(UUID id) {
        return backupJobRepository.findById(id).orElseThrow();
    }

    public BackupJob create(BackupJobDto dto) {
        BackupJob target = toEntity(new BackupJob(), dto);
        target.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        return backupJobRepository.save(target);
    }

    public BackupJob update(UUID id, BackupJobDto dto) {
        BackupJob target = backupJobRepository.findById(id).orElseThrow();
        toEntity(target, dto);
        return backupJobRepository.save(target);
    }

    public void delete(UUID id) {
        backupJobRepository.deleteById(id);
    }

    private BackupJob toEntity(BackupJob job, BackupJobDto dto) {
        job.setUser(userRepository.getReferenceById(dto.getUserId()));
        job.setTarget(backupTargetRepository.getReferenceById(dto.getTargetId()));
        job.setName(dto.getName());
        job.setNode(dto.getNode());
        job.setVmid(dto.getVmid());
        job.setGuestType(dto.getGuestType());
        job.setScheduleCron(dto.getScheduleCron());
        job.setCompression(dto.getCompression());
        job.setMode(dto.getMode());
        job.setEncrypted(dto.isEncrypted());
        job.setRetentionCount(dto.getRetentionCount());
        job.setRemoveAfter(dto.isRemoveAfter());
        job.setEnabled(dto.isEnabled());
        return job;
    }
}
