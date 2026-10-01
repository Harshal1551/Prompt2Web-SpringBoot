package com.prompt2web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectRequest {

    @NotBlank(message = "Project name is required")
    private String name;

    @NotBlank(message = "Initial prompt is required")
    private String initialPrompt;

    @NotBlank(message = "Framework is required")
    private String framework;
}