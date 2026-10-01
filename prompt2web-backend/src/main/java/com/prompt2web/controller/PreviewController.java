package com.prompt2web.controller;

import com.prompt2web.dto.PreviewResponse;
import com.prompt2web.service.PreviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/preview")
@RequiredArgsConstructor
public class PreviewController {

    private final PreviewService previewService;

    @PostMapping
    public ResponseEntity<PreviewResponse> startPreview(
            @PathVariable String projectId,
            Authentication authentication
    ) {

        String userId =
                authentication.getName();

        PreviewResponse response =
                previewService.startPreview(
                        projectId,
                        userId
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> stopPreview(
            @PathVariable String projectId
    ) {

        previewService.stopPreview(
                projectId
        );

        return ResponseEntity.noContent()
                .build();
    }
}