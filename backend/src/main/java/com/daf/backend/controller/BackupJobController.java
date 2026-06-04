package com.daf.backend.controller;

import com.daf.backend.model.BackupJob;
import com.daf.backend.model.BackupJobDto;
import com.daf.backend.model.BackupType;
import com.daf.backend.service.BackupExecutor;
import com.daf.backend.service.BackupJobService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
@AllArgsConstructor
public class BackupJobController {
    private final BackupJobService service;
    private final BackupExecutor executor;

    @GetMapping
    public ResponseEntity<List<BackupJob>> findAll() {
        List<BackupJob> targets = service.findAll();
        return ResponseEntity.ok(targets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BackupJob> findById(@PathVariable UUID id) {
        BackupJob target = service.findById(id);
        return ResponseEntity.ok(target);
    }

    @PostMapping
    public ResponseEntity<BackupJob> create(@RequestBody BackupJobDto dto) {
        BackupJob target = service.create(dto);
        return ResponseEntity.status(201).body(target);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BackupJob> update(@PathVariable UUID id, @RequestBody BackupJobDto dto) {
        BackupJob target = service.update(id, dto);
        return ResponseEntity.ok().body(target);
    }

    @PostMapping("/{id}/run")
    public ResponseEntity<?> run(@PathVariable UUID id, @RequestParam(defaultValue = "GUEST") BackupType backupType
    ) {
        BackupJob job = service.findById(id);
        executor.execute(job, backupType);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
