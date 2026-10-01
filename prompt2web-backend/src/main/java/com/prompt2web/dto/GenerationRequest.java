package com.prompt2web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerationRequest {

    @NotBlank(message = "Prompt is required")
    private String prompt;
}