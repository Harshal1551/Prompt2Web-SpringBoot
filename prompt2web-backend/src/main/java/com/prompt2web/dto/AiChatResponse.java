package com.prompt2web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AiChatResponse {

    private String message;
    private String fileId;
    private String updatedContent;
}