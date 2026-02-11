package com.apimaster.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "api_versions")
public class ApiVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_id", nullable = false)
    private Long apiId;

    @Column(nullable = false)
    private String version;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Api api;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
