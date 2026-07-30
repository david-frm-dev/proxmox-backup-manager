package com.daf.backend.service;

import com.daf.backend.interfaces.Service_I;
import com.daf.backend.model.BackupJob;
import com.daf.backend.dto.BackupJobDto;
import com.daf.backend.repository.BackupJobRepository;
import com.daf.backend.repository.BackupTargetRepository;
import com.daf.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
@AllArgsConstructor
public class BackupJobService implements Service_I<BackupJob, BackupJobDto> {
    private final BackupJobRepository backupJobRepository;
    private final UserRepository userRepository;
    private final BackupTargetRepository backupTargetRepository;

    @Override
    public List<BackupJob> findAll() {
        return backupJobRepository.findAll()    ;
    }

    @Override
    public BackupJob findById(UUID id) {
        return backupJobRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public BackupJob create(BackupJobDto dto) {
        BackupJob target = toEntity(new BackupJob(), dto);
        target.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        return backupJobRepository.save(target);
    }

    @Override
    public BackupJob update(UUID id, BackupJobDto dto) {
        BackupJob target = backupJobRepository.findById(id).orElseThrow();
        return backupJobRepository.save(toEntity(target, dto));
    }

    @Override
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
