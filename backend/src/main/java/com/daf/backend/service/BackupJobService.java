package com.daf.backend.service;

import com.daf.backend.model.BackupJob;
import com.daf.backend.model.BackupJobDto;
import com.daf.backend.repository.BackupJobRepository;
import com.daf.backend.repository.BackupTargetRepository;
import com.daf.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BackupJobService {
    private final BackupJobRepository backupJobRepository;
    private final UserRepository userRepository;
    private final BackupTargetRepository backupTargetRepository;

    /**
     * Finds every BackupJob
     *
     * @return A List of {@link BackupJob}
     * */
    public List<BackupJob> findAll() {
        return backupJobRepository.findAll();
    }

     /**
     * Finds a single {@link BackupJob} by its ID.
     *
     * @param id the ID of the BackupJob to look up
     * @return the BackupJob with the given ID
     * @throws java.util.NoSuchElementException if no BackupJob with the given ID exists
     */
    public BackupJob findById(UUID id) {
        return backupJobRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Creates a new {@link BackupJob} from the given DTO and persists it.
     *
     * @param dto the data used to create the BackupJob
     * @return the newly created and persisted BackupJob
     */
    public BackupJob create(BackupJobDto dto) {
        BackupJob target = toEntity(new BackupJob(), dto);
        target.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        return backupJobRepository.save(target);
    }

    /**
     * Updates a {@link BackupJob} from the given ID and DTO
     *
     * @param id is used to find the BackupJob
     * @param dto holds the changes values
     *
     * @return the updated BackupJob
     * */
    public BackupJob update(UUID id, BackupJobDto dto) {
        BackupJob target = backupJobRepository.findById(id).orElseThrow();
        return backupJobRepository.save(toEntity(target, dto));
    }

    /**
     * Deletes the BackupJob
     *
     * @param id is used to find the BackupJob
     * */
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
