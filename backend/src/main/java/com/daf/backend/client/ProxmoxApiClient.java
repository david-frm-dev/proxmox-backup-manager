package com.daf.backend.client;

import com.daf.backend.dto.NodeDto;
import com.daf.backend.model.BackupCompression;
import com.daf.backend.model.ProxmoxConnection;
import com.daf.backend.repository.ProxmoxConnectionRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Comparator;
import java.util.List;

@Component
@AllArgsConstructor
public class ProxmoxApiClient {
    private final ProxmoxConnectionRepository repository;

    public record ProxmoxResponse<T>(List<T> data) {}
    public record ProxmoxSingleResponse<T>(T data) {}

    public record QemuAndLxcDto(Integer vmid, String name, String status) {}
    public record TaskStatusDto(String status, String exitstatus) {}
    public record StorageContentDto(String volid, Integer vmid, Long ctime, String path) {}
    public record StorageContentDetailDto(String path, Long size) {}

    private WebClient buildConnection() {
        ProxmoxConnection proxmox = repository.findFirstBy().orElseThrow();
        String token = new String(proxmox.getTokenSecretEnc());

        String key = "PVEAPIToken=" + proxmox.getTokenId() + "=" + token;

        return WebClient.builder()
                .baseUrl(proxmox.getBaseUrl() + "/api2/json")
                .defaultHeader(HttpHeaders.AUTHORIZATION, key)
                .build();
    }

    public String startVzdump(String node, int vmid, BackupCompression compression) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("vmid", String.valueOf(vmid));
        body.add("compress", compression.name().toLowerCase());

        return buildConnection()
                .post()
                .uri("/nodes/{node}/vzdump", node)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ProxmoxSingleResponse<String>>() {})
                .block()
                .data();
    }

    public TaskStatusDto getTaskStatus(String node, String upid) {
        return buildConnection()
                .get()
                .uri("/nodes/{node}/tasks/{upid}/status", node, upid)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ProxmoxSingleResponse<TaskStatusDto>>() {})
                .block()
                .data();
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

    public String findLatestVolid(String node, String storage, int vmid) {
        return buildConnection()
                .get()
                .uri("/nodes/{node}/storage/{storage}/content", node, storage)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ProxmoxResponse<StorageContentDto>>() {})
                .map(ProxmoxResponse::data)
                .map(list -> list.stream()
                        .filter(e -> e.vmid() != null && vmid == e.vmid())
                        .max(Comparator.comparingLong(StorageContentDto::ctime))
                        .map(StorageContentDto::volid)
                        .orElseThrow()
                )
                .block();
    }

    public String getVolidPath(String node, String storage, String volid) {
        return buildConnection()
                .get()
                .uri("/nodes/{node}/storage/{storage}/content/{volid}", node, storage, volid)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ProxmoxSingleResponse<StorageContentDetailDto>>() {})
                .block()
                .data()
                .path();
    }
}
