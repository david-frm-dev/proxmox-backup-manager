package com.daf.backend.repository;

import com.daf.backend.model.BackupJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BackupJobRepository extends JpaRepository<BackupJob, UUID> {
}
