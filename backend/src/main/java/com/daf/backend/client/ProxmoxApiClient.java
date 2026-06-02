package com.daf.backend.client;

import com.daf.backend.dto.NodeDto;
import com.daf.backend.model.ProxmoxConnection;
import com.daf.backend.repository.ProxmoxConnectionRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@AllArgsConstructor
public class ProxmoxApiClient {
    private final ProxmoxConnectionRepository repository;

    public record ProxmoxResponse<T>(List<T> data) {}

    public record QemuAndLxcDto(Integer vmid, String name, String status) {}

    private WebClient buildConnection() {
        ProxmoxConnection proxmox = repository.findFirstBy().orElseThrow();
        String token = new String(proxmox.getTokenSecretEnc());

        String key = "PVEAPIToken=" + proxmox.getTokenId() + "=" + token;

        return WebClient.builder()
                .baseUrl(proxmox.getBaseUrl() + "/api2/json")
                .defaultHeader(HttpHeaders.AUTHORIZATION, key)
                .build();
    }

    public List<NodeDto> listNodes() {
        return buildConnection()
                .get()
                .uri("/nodes")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ProxmoxResponse<NodeDto>>() {})
                .map(ProxmoxResponse::data)
                .block();
    }

    public List<QemuAndLxcDto> listLxcs(String node) {
        return buildConnection()
                .get()
                .uri("/nodes/{node}/lxc", node)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ProxmoxResponse<QemuAndLxcDto>>() {})
                .map(ProxmoxResponse::data)
                .block();

    }

    public List<ProxmoxApiClient.QemuAndLxcDto> listQemu(String node) {
        return buildConnection()
                .get()
                .uri("/nodes/{node}/qemu", node)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ProxmoxResponse<QemuAndLxcDto>>() {})
                .map(ProxmoxResponse::data)
                .block();
    }
}
