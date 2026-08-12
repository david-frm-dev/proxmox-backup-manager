package com.daf.backend.model;

import com.daf.backend.enums.RestoreMode;
import com.daf.backend.enums.RestoreStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@Table(name = "restore_jobs")
public class RestoreJob {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "backup_record_id", nullable = false)
    @JsonIgnore
    private BackupRecord backupRecord;

    @Column(name = "target_node", nullable = false, length = 64)
    private String targetNode;

    @Column(name = "target_vmid", nullable = false)
    private Integer targetVmid;

    @Column(name = "mode", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private RestoreMode mode;

    @Column(name = "status", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private RestoreStatus status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "started_at")
    private Timestamp startedAt;

    @Column(name = "finished_at")
    private Timestamp finishedAt;
}
