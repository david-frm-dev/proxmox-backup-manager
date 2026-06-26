package com.daf.backend.service;

import com.daf.backend.model.BackupTarget;
import com.daf.backend.model.BackupTargetDto;
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
public class BackupTargetService {
    private final BackupTargetRepository backupTargetRepository;

    /**
     * Finds every BackupTarget
     *
     * @return A List of {@link BackupTarget}
     * */
    public List<BackupTarget> findAll() {
        return backupTargetRepository.findAll();
    }

    /**
     * Finds a single {@link BackupTarget} by its ID.
     *
     * @param id the ID of the BackupTarget to look up
     * @return the BackupTarget with the given ID
     * @throws java.util.NoSuchElementException if no BackupTarget with the given ID exists
     */
    public BackupTarget findById(UUID id) {
        return backupTargetRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Creates a new {@link BackupTarget} from the given DTO and persists it.
     *
     * @param dto the data used to create the BackupTarget
     * @return the newly created and persisted BackupTarget
     */
    public BackupTarget create(BackupTargetDto dto) {
        BackupTarget target = toEntity(new BackupTarget(), dto);
        target.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        return backupTargetRepository.save(target);
    }

    /**
     * Updates a {@link BackupTarget} from the given ID and DTO
     *
     * @param id is used to find the BackupTarget
     * @param dto holds the changes values
     *
     * @return the updated BackupTarget
     * */
    public BackupTarget update(UUID id, BackupTargetDto dto) {
        BackupTarget target = backupTargetRepository.findById(id).orElseThrow();
        return backupTargetRepository.save(toEntity(target, dto));
    }
    /**
     * Deletes the BackupTarget
     *
     * @param id is used to find the BackupTarget
     * */
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
