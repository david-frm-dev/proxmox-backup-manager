package com.daf.backend.service;

import com.daf.backend.interfaces.Service_I;
import com.daf.backend.model.BackupTarget;
import com.daf.backend.dto.BackupTargetDto;
import com.daf.backend.repository.BackupTargetRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BackupTargetService implements Service_I<BackupTarget, BackupTargetDto> {
    private final BackupTargetRepository backupTargetRepository;

    @Override
    public List<BackupTarget> findAll() {
        return backupTargetRepository.findAll();
    }

    @Override
    public BackupTarget findById(UUID id) {
        return backupTargetRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public BackupTarget create(BackupTargetDto dto) {
        BackupTarget target = toEntity(new BackupTarget(), dto);
        target.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        return backupTargetRepository.save(target);
    }

    @Override
    public BackupTarget update(UUID id, BackupTargetDto dto) {
        BackupTarget target = backupTargetRepository.findById(id).orElseThrow();
        return backupTargetRepository.save(toEntity(target, dto));
    }

    @Override
    public void delete(UUID id) {
        backupTargetRepository.deleteById(id);
    }

    private BackupTarget toEntity(BackupTarget target, BackupTargetDto dto) {
        target.setName(dto.getName());
        target.setType(dto.getType());
        target.setHost(dto.getHost());
        target.setPort(dto.getPort());
        target.setUsername(dto.getUsername());
        target.setCredentialsEnc(dto.getCredentials() != null ? dto.getCredentials().getBytes(StandardCharsets.UTF_8) : null);
        target.setBasePath(dto.getBasePath());
        return target;
    }
}
