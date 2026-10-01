package com.prompt2web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AiChatHistoryResponse {

    private String id;
    private String role;
    private String message;
    private String fileId;
    private LocalDateTime createdAt;
}