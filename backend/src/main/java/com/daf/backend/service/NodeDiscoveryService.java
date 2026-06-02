package com.daf.backend.service;

import com.daf.backend.client.ProxmoxApiClient;
import com.daf.backend.dto.NodeDto;
import com.daf.backend.model.Node;
import com.daf.backend.model.NodeStatus;
import com.daf.backend.repository.NodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@AllArgsConstructor
public class NodeDiscoveryService {
    private final ProxmoxApiClient proxmoxApiClient;
    private final NodeRepository nodeRepository;

    public List<Node> discoverNodes() {
        List<NodeDto> nodes = proxmoxApiClient.listNodes();

        for (NodeDto dto : nodes) {
            Node node = nodeRepository.findByName(dto.getNode()).orElse(new Node());

            NodeStatus status = NodeStatus.fromString(dto.getStatus());

            node.setName(dto.getNode());
            node.setStatus(status);
            node.setLastSeenAt(new Timestamp(System.currentTimeMillis()));

            nodeRepository.save(node);
        }

        return nodeRepository.findAll();
    }
}
