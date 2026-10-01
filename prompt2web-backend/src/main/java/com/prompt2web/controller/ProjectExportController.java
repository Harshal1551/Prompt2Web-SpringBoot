package com.prompt2web.controller;

import com.prompt2web.service.ProjectExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/export")
@RequiredArgsConstructor
public class ProjectExportController {

    private final ProjectExportService projectExportService;

    @GetMapping
    public ResponseEntity<byte[]> exportProject(
            @PathVariable String projectId,
            Authentication authentication
    ) {

        String userId = authentication.getName();

        byte[] zipFile =
                projectExportService.exportProject(
                        projectId,
                        userId
                );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"prompt2web-project.zip\""
                )
                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM
                )
                .contentLength(zipFile.length)
                .body(zipFile);
    }
}