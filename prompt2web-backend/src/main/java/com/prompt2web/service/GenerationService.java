package com.prompt2web.service;

import com.prompt2web.dto.GenerationRequest;
import com.prompt2web.dto.GenerationResponse;
import com.prompt2web.dto.GenerationStatusRequest;
import com.prompt2web.entity.Generation;
import com.prompt2web.entity.GenerationStatus;
import com.prompt2web.entity.Project;
import com.prompt2web.exception.ResourceNotFoundException;
import com.prompt2web.repository.GenerationRepository;
import com.prompt2web.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenerationService {

    private final GenerationRepository generationRepository;
    private final ProjectRepository projectRepository;
    private final OpenRouterService openRouterService;
    private final GeneratedFileService generatedFileService;
    private final GenerationWorkerService generationWorkerService;

    // ============================================================
    // CREATE GENERATION
    // ============================================================

    public GenerationResponse createGeneration(
            String projectId,
            String userId,
            GenerationRequest request
    ) {

        // 1. Check that project belongs to logged-in user
        Project project = projectRepository
                .findByIdAndUserId(projectId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));


        // 2. Create generation
        Generation generation = Generation.builder()
                .prompt(request.getPrompt())
                .status(GenerationStatus.PENDING)
                .project(project)
                .build();


        // 3. Save generation
        Generation savedGeneration =
                generationRepository.save(generation);


        // 4. Return response
        return toGenerationResponse(savedGeneration);
    }


    // ============================================================
    // GET SINGLE GENERATION
    // ============================================================

    public GenerationResponse getGeneration(
            String projectId,
            String generationId,
            String userId
    ) {

        // 1. Check project ownership
        projectRepository
                .findByIdAndUserId(projectId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));


        // 2. Find generation inside project
        Generation generation = generationRepository
                .findByIdAndProjectId(generationId, projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Generation not found"));


        // 3. Return response
        return toGenerationResponse(generation);
    }


    // ============================================================
    // GET ALL GENERATIONS OF PROJECT
    // ============================================================

    public List<GenerationResponse> getProjectGenerations(
            String projectId,
            String userId
    ) {

        // 1. Check project ownership
        projectRepository
                .findByIdAndUserId(projectId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));


        // 2. Get all generations
        List<Generation> generations =
                generationRepository.findByProjectId(projectId);


        // 3. Convert entities to responses
        return generations.stream()
                .map(this::toGenerationResponse)
                .toList();
    }


    // ============================================================
    // UPDATE GENERATION STATUS
    // ============================================================

    public GenerationResponse updateGenerationStatus(
            String projectId,
            String generationId,
            String userId,
            GenerationStatusRequest request
    ) {

        // 1. Check project ownership
        projectRepository
                .findByIdAndUserId(projectId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));


        // 2. Find generation
        Generation generation = generationRepository
                .findByIdAndProjectId(generationId, projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Generation not found"));


        // 3. Get requested status
        GenerationStatus newStatus = GenerationStatus.valueOf(
                request.getStatus().toUpperCase()
        );


        // 4. Update status
        generation.setStatus(newStatus);


        // 5. Set completedAt only when generation finishes
        if (newStatus == GenerationStatus.COMPLETED
                || newStatus == GenerationStatus.FAILED) {

            generation.setCompletedAt(
                    LocalDateTime.now()
            );

        } else {

            // PENDING / IN_PROGRESS
            generation.setCompletedAt(null);
        }


        // 6. Save updated generation
        Generation updatedGeneration =
                generationRepository.save(generation);


        // 7. Return response
        return toGenerationResponse(updatedGeneration);
    }

    // ============================================================
// START GENERATION
// ============================================================

    public GenerationResponse startGeneration(
            String projectId,
            String generationId,
            String userId
    ) {

        // 1. Verify project belongs to logged-in user
        projectRepository
                .findByIdAndUserId(projectId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"
                        )
                );

        // 2. Find generation
        Generation generation = generationRepository
                .findByIdAndProjectId(generationId, projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Generation not found"
                        )
                );

        // 3. Generation must be PENDING
        if (generation.getStatus() != GenerationStatus.PENDING) {
            throw new IllegalStateException(
                    "Generation can only be started when status is PENDING"
            );
        }

        // 4. Immediately change status to IN_PROGRESS
        generation.setStatus(
                GenerationStatus.IN_PROGRESS
        );

        Generation updatedGeneration =
                generationRepository.save(generation);

        // 5. Start AI generation in background
        generationWorkerService.processGeneration(
                projectId,
                generationId,
                userId
        );

        // 6. Immediately return IN_PROGRESS
        return toGenerationResponse(
                updatedGeneration
        );
    }


    // ============================================================
    // ENTITY -> RESPONSE CONVERTER
    // ============================================================

    private GenerationResponse toGenerationResponse(
            Generation generation
    ) {

        return new GenerationResponse(
                generation.getId(),
                generation.getPrompt(),

                // GenerationStatus enum -> String
                generation.getStatus().name(),

                generation.getProject().getId(),
                generation.getCreatedAt(),
                generation.getCompletedAt()
        );
    }
}