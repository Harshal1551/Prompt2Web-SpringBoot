package com.prompt2web.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "generations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Generation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String prompt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenerationStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();

        if (status == null) {
            status = GenerationStatus.PENDING;
        }
    }
}