package com.daf.backend.model;

import com.daf.backend.enums.BackupCompression;
import com.daf.backend.enums.BackupStatus;
import com.daf.backend.enums.BackupType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@Table(name = "backup_records")
public class BackupRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = true)
    private BackupJob job;

    @Column(name = "type", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private BackupType type;

    @Column(name = "node",length = 64, nullable = false)
    private String node;

    @Column(name = "vmid")
    private Integer vmid;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "remote_path", nullable = false)
    private String remotePath;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(name = "compression")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private BackupCompression compression;

    @Column(name = "encrypted", nullable = false)
    private Boolean encrypted;

    @Column(name = "sha256_orig", length = 64)
    private String sha256Orig;

    @Column(name = "sha256_enc", length = 64)
    private String sha256Enc;

    @Column(name = "status", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private BackupStatus status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "started_at")
    private Timestamp startedAt;

    @Column(name = "finished_at")
    private Timestamp finishedAt;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "verified_at")
    private Timestamp verifiedAt;

}
