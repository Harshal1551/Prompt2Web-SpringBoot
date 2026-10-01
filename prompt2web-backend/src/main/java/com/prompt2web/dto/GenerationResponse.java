package com.prompt2web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GenerationResponse {

    private String id;
    private String prompt;
    private String status;
    private String projectId;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}