package com.daf.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "username", length = 255,  nullable = false)
    private String username;

    @Column(name = "email", length = 255,  nullable = true)
    private String email;

    @Column(name = "password_hash", columnDefinition = "CHAR(60)", nullable = false)
    private String passwordHash;

    @Column(name = "bkp_key_enc")
    private byte[] bkp_key_enc;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "last_login_at")
    private Timestamp lastLoginAt;
}
