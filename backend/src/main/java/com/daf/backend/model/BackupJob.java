package com.daf.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@Table(name = "backup_jobs")
public class BackupJob {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private BackupTarget target;

    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Column(name = "node", length = 64, nullable = false)
    private String node;

    @Column(name = "vmid", nullable = false)
    private Integer vmid;

    @Column(name = "guest_type", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private GuestType guestType;

    @Column(name = "schedule_cron", length = 64, nullable = false)
    private String scheduleCron;

    @Column(name = "compression", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private BackupCompression compression;

    @Column(name = "mode", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private DumpMode mode;

    @Column(name = "encrypted", nullable = false)
    private boolean encrypted;

    @Column(name = "retention_count", nullable = false)
    private Integer retentionCount;

    @Column(name = "remove_after", nullable = false)
    private boolean removeAfter;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "last_run_at")
    private Timestamp lastRunAt;

    @Column(name = "next_run_at")
    private Timestamp nextRunAt;

    @Column(name = "created_at")
    private Timestamp createdAt;

}
