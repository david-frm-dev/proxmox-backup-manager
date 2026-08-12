package com.daf.backend.model;

import com.daf.backend.enums.NodeStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@Table(name = "nodes")
public class Node {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "status", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private NodeStatus status;

    @Column(name = "last_seen_at", nullable = false)
    private Timestamp lastSeenAt;
}
