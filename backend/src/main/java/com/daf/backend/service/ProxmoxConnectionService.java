package com.daf.backend.service;

import com.daf.backend.model.ProxmoxConnection;
import com.daf.backend.repository.ProxmoxConnectionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@AllArgsConstructor
public class ProxmoxConnectionService {
    private final ProxmoxConnectionRepository proxmoxConnectionRepository;

    public record ProxmoxConnectionRequest(
            String baseUrl,
            String tokenId,
            String tokenSecret,
            boolean verifyTls,
            String sshUser,
            String sshKey
    ) {}

    public ProxmoxConnection save(ProxmoxConnectionRequest proxmoxConnectionRequest) {
        ProxmoxConnection proxConnection = proxmoxConnectionRepository
                .findFirstBy()
                .orElse(new ProxmoxConnection());

        proxConnection.setBaseUrl(proxmoxConnectionRequest.baseUrl());
        proxConnection.setTokenId(proxmoxConnectionRequest.tokenId());
        proxConnection.setTokenSecretEnc(proxmoxConnectionRequest.tokenSecret().getBytes());
        proxConnection.setVerifyTls(proxmoxConnectionRequest.verifyTls());
        proxConnection.setSshUser(proxmoxConnectionRequest.sshUser());
        proxConnection.setSshKeyEnc(proxmoxConnectionRequest.sshKey().getBytes());

        if (proxConnection.getCreatedAt() == null) {
            proxConnection.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }

        return this.proxmoxConnectionRepository.save(proxConnection);
    }
}