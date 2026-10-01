package com.prompt2web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerationStatusRequest {

    @NotBlank(message = "Status is required")
    private String status;
}