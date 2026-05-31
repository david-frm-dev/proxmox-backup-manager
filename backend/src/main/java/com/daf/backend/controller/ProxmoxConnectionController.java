package com.daf.backend.controller;


import com.daf.backend.model.ProxmoxConnection;
import com.daf.backend.service.ProxmoxConnectionService;
import com.daf.backend.service.ProxmoxConnectionService.ProxmoxConnectionRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/proxmox-connection")
@AllArgsConstructor
public class ProxmoxConnectionController {
    private final ProxmoxConnectionService proxmoxConnectionService;

    @PostMapping
    public ResponseEntity<ProxmoxConnection> save(@RequestBody ProxmoxConnectionRequest proxmoxConnectionRequest) {
        ProxmoxConnection proxmoxConnection = this.proxmoxConnectionService.save(proxmoxConnectionRequest);

        return ResponseEntity.created(URI.create("/api/proxmox/connection"))
                .body(proxmoxConnection);
    }
}
