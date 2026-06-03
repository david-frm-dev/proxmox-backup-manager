package com.daf.backend.model;

import lombok.Data;

import java.util.UUID;

@Data
public class BackupJobDto {
    private UUID userId;
    private String name;
    private String node;
    private Integer vmid;
    private GuestType guestType;
    private String scheduleCron;
    private BackupCompression compression;
    private DumpMode mode;
    private boolean encrypted;
    private Integer retentionCount;
    private boolean removeAfter;
    private UUID targetId;
    private boolean enabled;
}
