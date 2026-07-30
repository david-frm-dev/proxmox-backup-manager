package com.daf.backend.service;

import com.daf.backend.model.*;
import com.daf.backend.repository.BackupJobRepository;
import com.daf.backend.repository.BackupTargetRepository;
import com.daf.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BackupJobServiceTest {
    @Mock
    private BackupJobRepository  backupJobRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BackupTargetRepository backupTargetRepository;

    @Test
    void findAllPositive() {
        List<BackupJob> jobs = generateMoreTestBackupJobs(4);
        if (jobs.isEmpty()) throw new NullPointerException("Jobs is empty");

        when(backupJobRepository.findAll()).thenReturn(jobs);

        List<BackupJob> backupJobs = backupJobRepository.findAll();

        assertNotNull(backupJobs);
        assertEquals(jobs.size(), backupJobs.size());
    }

    /**
     * Check for null and not emptyList
     * Maybe delete because shit test
     * */
    @Test
    void findAllNegative() {
        List<BackupJob> jobs = generateMoreTestBackupJobs(0);
        when(backupJobRepository.findAll()).thenReturn(jobs);

        List<BackupJob> backupJobs = backupJobRepository.findAll();

        assertNull(backupJobs);
        assertDoesNotThrow(() -> backupJobRepository.findAll());
    }

    @Test
    void findById() {
        BackupJob mockJob = buildTestBackupJob();
        when(backupJobRepository.findById(mockJob.getId())).thenReturn(Optional.of(mockJob));

        Optional<BackupJob> backupjob = backupJobRepository.findById(mockJob.getId());

        assertTrue(backupjob.isPresent());
    }

    @Test
    void create() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    private BackupJob buildTestBackupJob() {
        UUID jobId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        User testUser = new User();
        BackupTarget testTarget;
        testUser.setId(userId);

        testTarget = new BackupTarget();
        testTarget.setId(targetId);

        BackupJob job = new BackupJob();
        job.setId(jobId);
        job.setUser(testUser);
        job.setTarget(testTarget);
        job.setName(UUID.randomUUID().toString());
        job.setNode("pve-node01");
        job.setVmid((int) (Math.random() * 1001));
        job.setGuestType(GuestType.QEMU);
        job.setScheduleCron("0 30 2 * * *");
        job.setCompression(BackupCompression.ZSTD);
        job.setMode(DumpMode.SNAPSHOT);
        job.setEncrypted(true);
        job.setRetentionCount(7);
        job.setRemoveAfter(false);
        job.setEnabled(true);
        job.setLastRunAt(Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
        job.setNextRunAt(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
        job.setCreatedAt(Timestamp.valueOf(LocalDateTime.now().minusDays(30)));
        return job;
    }

    private List<BackupJob> generateMoreTestBackupJobs(int count) {
        if (count < 1) return null;
        List<BackupJob> backupJobs = new ArrayList<BackupJob>(count);

        for (int i = 0; i < count; i++) {
            backupJobs.add(buildTestBackupJob());
        }

        return backupJobs;
    }
}