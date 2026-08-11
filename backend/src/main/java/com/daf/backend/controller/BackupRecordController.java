package com.daf.backend.controller;

import com.daf.backend.model.BackupRecord;
import com.daf.backend.service.BackupRecordService;
import com.daf.backend.service.VerificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/backups")
@AllArgsConstructor
public class BackupRecordController {
    private final BackupRecordService service;
    private final VerificationService verificationService;

    @GetMapping()
    public ResponseEntity<List<BackupRecord>> findAll(@RequestParam(required = false) String node, @RequestParam(required = false) Integer vmid) {
        return ResponseEntity.ok(service.findAll(node, vmid));
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<BackupRecord> verify(@PathVariable UUID id) throws Exception {
        return ResponseEntity.ok(verificationService.verify(id));
    }
}
