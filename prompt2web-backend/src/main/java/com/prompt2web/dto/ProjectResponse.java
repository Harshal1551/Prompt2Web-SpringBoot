package com.prompt2web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProjectResponse {

    private String id;
    private String name;
    private String initialPrompt;
    private String framework;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}