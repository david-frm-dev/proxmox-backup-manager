package com.daf.backend.repository;

import com.daf.backend.model.ProxmoxConnection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProxmoxConnectionRepository extends JpaRepository<ProxmoxConnection, UUID> {
    Optional<ProxmoxConnection> findFirstBy();}
