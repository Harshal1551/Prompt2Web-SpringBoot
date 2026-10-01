package com.prompt2web.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String initialPrompt;

    private String framework;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<ProjectFile> files = new ArrayList<>();

    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Generation> generations = new ArrayList<>();

    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<AiChatMessage> aiChatMessages = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}