package com.kjt.springsecurity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = true, length = 100)
    private String email;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    // ABAC Attributes
    @Column(name = "department", length = 50)
    private String department; // e.g., "IT", "HR", "FINANCE"

    @Column(name = "position", length = 50)
    private String position; // e.g., "EMPLOYEE", "MANAGER", "DIRECTOR"

    @Column(name = "clearance_level", nullable = false)
    private Integer clearanceLevel = 1; // 1-5, higher = more access

    @OneToMany(mappedBy = "user")
    private Set<UserRole> userRoles = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserAttribute> userAttributes = new LinkedHashSet<>();

    @OneToMany(mappedBy = "owner")
    private Set<Document> documents = new LinkedHashSet<>();

}