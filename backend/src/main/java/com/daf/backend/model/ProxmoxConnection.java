package com.daf.backend.model;

import com.daf.backend.converter.CryptoConverter;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "proxmox_connection")
@Data
public class ProxmoxConnection {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "base_url", nullable = false)
    private String baseUrl;

    @Column(name = "token_id", nullable = false)
    private String tokenId;

    @Convert(converter = CryptoConverter.class)
    @Column(name = "token_secret_enc", nullable = false)
    private byte[] tokenSecretEnc;

    @Column(name = "verify_tls", nullable = false)
    private boolean verifyTls = true;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "ssh_user")
    private String sshUser;

    @Convert(converter = CryptoConverter.class)
    @Column(name = "ssh_key_enc")
    private byte[] sshKeyEnc;

    @Column(name = "ssh_ip_address")
    private String sshIpAddress;

    @Column(name = "ssh_port")
    private Integer sshPort;
}
