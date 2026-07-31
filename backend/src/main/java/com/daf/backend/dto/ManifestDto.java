package com.daf.backend.dto;

import lombok.Builder;

import java.sql.Timestamp;

public record ManifestDto(int vmid, String filename, long sizeBytes, String sha256Orig, String sha256Enc,
                          boolean encrypted, Timestamp startAt) {
}
