package com.prompt2web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProjectFileResponse {

    private String id;
    private String filePath;
    private String fileName;
    private String content;
    private String language;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}