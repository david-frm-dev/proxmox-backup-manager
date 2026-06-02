package com.daf.backend.controller;

import com.daf.backend.client.ProxmoxApiClient;
import com.daf.backend.dto.NodeDto;
import com.daf.backend.model.Node;
import com.daf.backend.service.NodeDiscoveryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nodes")
@AllArgsConstructor
public class NodeController {
    private final ProxmoxApiClient apiClient;
    private final NodeDiscoveryService discoveryService;

    @GetMapping
    public ResponseEntity<List<NodeDto>> getListNodes() {
        List<NodeDto> nodes = apiClient.listNodes();
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/{node}/lxc")
    public ResponseEntity<List<ProxmoxApiClient.QemuAndLxcDto>> getListLxc(@PathVariable String node) {
        List<ProxmoxApiClient.QemuAndLxcDto> nodes = apiClient.listLxcs(node);
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/{node}/qemu")
    public ResponseEntity<List<ProxmoxApiClient.QemuAndLxcDto>> getListQemu(@PathVariable String node) {
        List<ProxmoxApiClient.QemuAndLxcDto> nodes = apiClient.listQemu(node);
        return ResponseEntity.ok(nodes);
    }

    @PostMapping("/discover")
    public ResponseEntity<List<Node>> discover() {
        List<Node> nodes = discoveryService.discoverNodes();
        return ResponseEntity.ok(nodes);
    }
}
