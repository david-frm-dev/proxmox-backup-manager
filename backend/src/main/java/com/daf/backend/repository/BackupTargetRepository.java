package com.daf.backend.repository;

import com.daf.backend.model.BackupTarget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BackupTargetRepository extends JpaRepository<BackupTarget, UUID> {

}
