package com.daf.backend.repository;

import com.daf.backend.model.Node;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NodeRepository extends JpaRepository<Node, UUID> {
    Optional<Node> findByName(String name);
}
