package com.prompt2web.controller;

import com.prompt2web.dto.ProjectFileRequest;
import com.prompt2web.dto.ProjectFileResponse;
import com.prompt2web.service.ProjectFileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/files")
@RequiredArgsConstructor
public class ProjectFileController {

    private final ProjectFileService projectFileService;

    @PostMapping
    public ResponseEntity<ProjectFileResponse> createFile(
            @PathVariable String projectId,
            @Valid @RequestBody ProjectFileRequest request,
            Authentication authentication
    ) {

        String userId = authentication.getName();

        ProjectFileResponse response =
                projectFileService.createFile(
                        projectId,
                        userId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping
    public ResponseEntity<List<ProjectFileResponse>> getProjectFiles(
            @PathVariable String projectId,
            Authentication authentication
    ) {

        String userId = authentication.getName();

        List<ProjectFileResponse> files =
                projectFileService.getProjectFiles(
                        projectId,
                        userId
                );

        return ResponseEntity.ok(files);
    }


    @GetMapping("/{fileId}")
    public ResponseEntity<ProjectFileResponse> getFile(
            @PathVariable String projectId,
            @PathVariable String fileId,
            Authentication authentication
    ) {

        String userId = authentication.getName();

        ProjectFileResponse response =
                projectFileService.getFile(
                        projectId,
                        fileId,
                        userId
                );

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{fileId}")
    public ResponseEntity<ProjectFileResponse> updateFile(
            @PathVariable String projectId,
            @PathVariable String fileId,
            @Valid @RequestBody ProjectFileRequest request,
            Authentication authentication
    ) {

        String userId = authentication.getName();

        ProjectFileResponse response =
                projectFileService.updateFile(
                        projectId,
                        fileId,
                        userId,
                        request
                );

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable String projectId,
            @PathVariable String fileId,
            Authentication authentication
    ) {

        String userId = authentication.getName();

        projectFileService.deleteFile(
                projectId,
                fileId,
                userId
        );

        return ResponseEntity.noContent().build();
    }





}