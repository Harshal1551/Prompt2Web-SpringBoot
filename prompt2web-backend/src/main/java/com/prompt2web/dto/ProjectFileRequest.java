package com.prompt2web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectFileRequest {

    @NotBlank(message = "File path is required")
    private String filePath;

    @NotBlank(message = "File name is required")
    private String fileName;

    @NotBlank(message = "File content is required")
    private String content;

    private String language;
}