package com.prompt2web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiChatRequest {

    @NotBlank(message = "Message is required")
    private String message;

    private String fileId;
}