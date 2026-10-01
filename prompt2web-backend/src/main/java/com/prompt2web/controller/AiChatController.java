package com.prompt2web.controller;

import com.prompt2web.dto.AiChatHistoryResponse;
import com.prompt2web.dto.AiChatRequest;
import com.prompt2web.dto.AiChatResponse;
import com.prompt2web.service.AiChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/ai")
@RequiredArgsConstructor
public class AiChatController {

    private final AiChatService aiChatService;

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(
            @PathVariable String projectId,
            @Valid @RequestBody AiChatRequest request,
            Authentication authentication
    ) {

        String userId = authentication.getName();

        AiChatResponse response =
                aiChatService.chat(
                        projectId,
                        userId,
                        request
                );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/chat")
    public ResponseEntity<List<AiChatHistoryResponse>> getChatHistory(
            @PathVariable String projectId,
            Authentication authentication
    ) {

        String userId = authentication.getName();

        List<AiChatHistoryResponse> history =
                aiChatService.getChatHistory(
                        projectId,
                        userId
                );

        return ResponseEntity.ok(history);
    }



}