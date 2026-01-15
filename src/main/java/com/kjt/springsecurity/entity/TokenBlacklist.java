package com.kjt.springsecurity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "token_blacklist")
public class TokenBlacklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "token", nullable = false, unique = true, length = 500)
    private String token;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @Column(name = "blacklisted_at", nullable = false)
    private Instant blacklistedAt;

    @Column(name = "reason", length = 100)
    private String reason;

    @PrePersist
    protected void onCreate() {
        blacklistedAt = Instant.now();
    }
}
