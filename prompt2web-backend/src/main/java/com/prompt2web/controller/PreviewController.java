package com.prompt2web.controller;

import com.prompt2web.dto.PreviewResponse;
import com.prompt2web.service.PreviewService;
import jakarta.servlet.http.HttpServletRequest;
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
            Authentication authentication,
            HttpServletRequest request
    ) {

        String userId =
                authentication.getName();

        String baseUrl =
                request.getScheme()
                        + "://"
                        + request.getServerName();

        int port =
                request.getServerPort();

        if (port != 80 && port != 443) {
            baseUrl += ":" + port;
        }

        PreviewResponse response =
                previewService.startPreview(
                        projectId,
                        userId,
                        baseUrl
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

        return ResponseEntity
                .noContent()
                .build();
    }


    @GetMapping("/public/{token}")
    public ResponseEntity<byte[]> previewRoot(
            @PathVariable String projectId,
            @PathVariable String token,
            HttpServletRequest request
    ) {

        return previewService.proxyPreview(
                projectId,
                token,
                "/",
                request
        );
    }


    @GetMapping("/public/{token}/")
    public ResponseEntity<byte[]> previewRootWithSlash(
            @PathVariable String projectId,
            @PathVariable String token,
            HttpServletRequest request
    ) {

        return previewService.proxyPreview(
                projectId,
                token,
                "/",
                request
        );
    }


    @GetMapping("/public/{token}/{*path}")
    public ResponseEntity<byte[]> previewResource(
            @PathVariable String projectId,
            @PathVariable String token,
            @PathVariable String path,
            HttpServletRequest request
    ) {

        return previewService.proxyPreview(
                projectId,
                token,
                path,
                request
        );
    }
}