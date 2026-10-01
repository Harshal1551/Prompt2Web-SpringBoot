package com.prompt2web.controller;

import com.prompt2web.dto.ProjectRequest;
import com.prompt2web.dto.ProjectResponse;
import com.prompt2web.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication
    ) {

        String userId = authentication.getName();

        ProjectResponse response = projectService.createProject(
                userId,
                request.getName(),
                request.getInitialPrompt(),
                request.getFramework()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getMyProjects(
            Authentication authentication
    ) {

        String userId = authentication.getName();

        List<ProjectResponse> projects =
                projectService.getUserProjects(userId);

        return ResponseEntity.ok(projects);
    }


    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable String projectId,
            Authentication authentication
    ) {

        String userId = authentication.getName();

        ProjectResponse response =
                projectService.getProjectById(projectId, userId);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable String projectId,
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication
    ) {

        String userId = authentication.getName();

        ProjectResponse response = projectService.updateProject(
                projectId,
                userId,
                request.getName(),
                request.getInitialPrompt(),
                request.getFramework()
        );

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable String projectId,
            Authentication authentication
    ) {

        String userId = authentication.getName();

        projectService.deleteProject(projectId, userId);

        return ResponseEntity.noContent().build();
    }





}