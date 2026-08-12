package com.daf.backend.dto;

import com.daf.backend.enums.RestoreMode;
import lombok.Data;

@Data
public class RestoreRequestDto {
    private String targetNode;
    private Integer targetVmid;
    private RestoreMode restoreMode;
}
