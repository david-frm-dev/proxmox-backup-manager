package com.daf.backend.model;

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

    @Column(name = "base_url", length = 255, nullable = false)
    private String baseUrl;

    @Column(name = "token_id", length = 255, nullable = false)
    private String tokenId;

    @Column(name = "token_secret_enc", length = 255, nullable = false)
    private byte[] tokenSecretEnc;

    @Column(name = "verify_tls", nullable = false)
    private boolean verifyTls = true;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "ssh_user")
    private String sshUser;

    @Column(name = "ssh_key_enc")
    private byte[] sshKeyEnc;

    @Column(name = "ssh_ip_address")
    private String sshIpAddress;

    @Column(name = "ssh_port")
    private Integer sshPort;
}
