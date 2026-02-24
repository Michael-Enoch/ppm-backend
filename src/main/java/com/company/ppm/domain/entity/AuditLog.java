package com.company.ppm.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "actor_email", length = 190)
    private String actorEmail;

    @Column(nullable = false, length = 120)
    private String action;

    @Column(nullable = false, length = 200)
    private String resource;

    @Column(name = "resource_id", length = 120)
    private String resourceId;

    @Column(nullable = false, length = 10)
    private String method;

    @Column(nullable = false, length = 255)
    private String path;

    @Column(length = 4000)
    private String payload;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onPersist() {
        createdAt = Instant.now();
    }
}
