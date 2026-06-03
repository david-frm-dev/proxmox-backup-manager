package com.daf.backend.controller;

import com.daf.backend.model.BackupTarget;
import com.daf.backend.model.BackupTargetDto;
import com.daf.backend.service.BackupTargetService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/targets")
@AllArgsConstructor
public class BackupTargetController {
    private final BackupTargetService service;

    @GetMapping
    public ResponseEntity<List<BackupTarget>> findAll() {
        List<BackupTarget> targets = service.findAll();
        return ResponseEntity.ok(targets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BackupTarget> findById(@PathVariable UUID id) {
        BackupTarget target = service.findById(id);
        return ResponseEntity.ok(target);
    }

    @PostMapping
    public ResponseEntity<BackupTarget> create(@RequestBody BackupTargetDto dto) {
        BackupTarget target = service.create(dto);
        return ResponseEntity.status(201).body(target);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BackupTarget> update(@PathVariable UUID id, @RequestBody BackupTargetDto dto) {
        BackupTarget target = service.update(id, dto);
        return ResponseEntity.ok().body(target);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
