package com.prompt2web.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private String role;

    @Column(columnDefinition = "TEXT")
    private String fileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}