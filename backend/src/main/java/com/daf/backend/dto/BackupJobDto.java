package com.daf.backend.dto;

import com.daf.backend.enums.BackupCompression;
import com.daf.backend.enums.DumpMode;
import com.daf.backend.enums.GuestType;
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
    private String storage;
    private Integer retentionCount;
    private boolean removeAfter;
    private UUID targetId;
    private boolean enabled;
}
