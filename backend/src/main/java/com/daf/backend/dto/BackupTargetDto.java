package com.daf.backend.dto;

import com.daf.backend.model.TargetType;
import lombok.Data;

@Data
public class BackupTargetDto {
    private String name;
    private TargetType type;
    private String host;
    private Integer port;
    private String username;
    private String credentials;
    private String basePath;
}
