package com.prompt2web.controller;

import com.prompt2web.dto.GenerationRequest;
import com.prompt2web.dto.GenerationResponse;
import com.prompt2web.dto.GenerationStatusRequest;
import com.prompt2web.service.GenerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/generations")
@RequiredArgsConstructor
public class GenerationController {

    private final GenerationService generationService;

    @PostMapping
    public ResponseEntity<GenerationResponse> createGeneration(
            @PathVariable String projectId,
            @Valid @RequestBody GenerationRequest request,
            Authentication authentication
    ) {

        String userId = authentication.getName();

        GenerationResponse response =
                generationService.createGeneration(
                        projectId,
                        userId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping("/{generationId}")
    public ResponseEntity<GenerationResponse> getGeneration(
            @PathVariable String projectId,
            @PathVariable String generationId,
            Authentication authentication
    ) {

        String userId = authentication.getName();

        GenerationResponse response =
                generationService.getGeneration(
                        projectId,
                        generationId,
                        userId
                );

        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<List<GenerationResponse>> getProjectGenerations(
            @PathVariable String projectId,
            Authentication authentication
    ) {

        String userId = authentication.getName();

        List<GenerationResponse> generations =
                generationService.getProjectGenerations(
                        projectId,
                        userId
                );

        return ResponseEntity.ok(generations);
    }


    @PutMapping("/{generationId}/status")
    public ResponseEntity<GenerationResponse> updateGenerationStatus(
            @PathVariable String projectId,
            @PathVariable String generationId,
            @Valid @RequestBody GenerationStatusRequest request,
            Authentication authentication
    ) {

        String userId = authentication.getName();

        GenerationResponse response =
                generationService.updateGenerationStatus(
                        projectId,
                        generationId,
                        userId,
                        request
                );

        return ResponseEntity.ok(response);
    }


    @PostMapping("/{generationId}/start")
    public ResponseEntity<GenerationResponse> startGeneration(
            @PathVariable String projectId,
            @PathVariable String generationId,
            Authentication authentication
    ) {

        String userId = authentication.getName();

        GenerationResponse response =
                generationService.startGeneration(
                        projectId,
                        generationId,
                        userId
                );

        return ResponseEntity.ok(response);
    }





}