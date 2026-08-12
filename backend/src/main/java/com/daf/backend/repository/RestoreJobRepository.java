package com.daf.backend.repository;

import com.daf.backend.model.RestoreJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RestoreJobRepository extends JpaRepository<RestoreJob, UUID> {

}
