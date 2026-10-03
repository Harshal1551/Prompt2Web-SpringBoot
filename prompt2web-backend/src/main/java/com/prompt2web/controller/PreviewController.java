package com.prompt2web.controller;

import com.prompt2web.dto.PreviewResponse;
import com.prompt2web.service.PreviewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/preview")
@RequiredArgsConstructor
public class PreviewController {

    private final PreviewService previewService;


    // ============================================================
    // START PREVIEW
    // ============================================================

    @PostMapping
    public ResponseEntity<PreviewResponse> startPreview(
            @PathVariable String projectId,
            Authentication authentication,
            HttpServletRequest request
    ) {

        String userId = authentication.getName();

        PreviewResponse response =
                previewService.startPreview(
                        projectId,
                        userId
                );

        return ResponseEntity.ok(response);
    }


    // ============================================================
    // STOP PREVIEW
    // ============================================================

    @DeleteMapping
    public ResponseEntity<Void> stopPreview(
            @PathVariable String projectId,
            Authentication authentication
    ) {

        String userId = authentication.getName();

        previewService.stopPreview(
                projectId,
                userId
        );

        return ResponseEntity
                .noContent()
                .build();
    }


    // ============================================================
    // PUBLIC PREVIEW ROOT
    // ============================================================

    @GetMapping("/public/{token}")
    public ResponseEntity<Resource> previewRoot(
            @PathVariable String projectId,
            @PathVariable String token
    ) {

        return previewService.servePreviewFile(
                projectId,
                token,
                "/"
        );
    }


    // ============================================================
    // PUBLIC PREVIEW ROOT WITH SLASH
    // ============================================================

    @GetMapping("/public/{token}/")
    public ResponseEntity<Resource> previewRootWithSlash(
            @PathVariable String projectId,
            @PathVariable String token
    ) {

        return previewService.servePreviewFile(
                projectId,
                token,
                "/"
        );
    }


    // ============================================================
    // PUBLIC PREVIEW RESOURCE
    // ============================================================

    @GetMapping("/public/{token}/{*path}")
    public ResponseEntity<Resource> previewResource(
            @PathVariable String projectId,
            @PathVariable String token,
            @PathVariable String path
    ) {

        return previewService.servePreviewFile(
                projectId,
                token,
                path
        );
    }
}