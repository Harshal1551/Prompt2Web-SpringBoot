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
    private final GenerationWorkerService generationWorkerService;


    // ============================================================
    // CREATE GENERATION
    // ============================================================

    public GenerationResponse createGeneration(
            String projectId,
            String userId,
            GenerationRequest request
    ) {

        Project project =
                projectRepository
                        .findByIdAndUserId(
                                projectId,
                                userId
                        )
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Project not found"
                                )
                        );


        Generation generation =
                Generation.builder()
                        .prompt(request.getPrompt())
                        .status(GenerationStatus.PENDING)
                        .project(project)
                        .build();


        Generation savedGeneration =
                generationRepository.save(
                        generation
                );


        return toGenerationResponse(
                savedGeneration
        );
    }


    // ============================================================
    // GET SINGLE GENERATION
    // ============================================================

    public GenerationResponse getGeneration(
            String projectId,
            String generationId,
            String userId
    ) {

        projectRepository
                .findByIdAndUserId(
                        projectId,
                        userId
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Project not found"
                        )
                );


        Generation generation =
                generationRepository
                        .findByIdAndProjectId(
                                generationId,
                                projectId
                        )
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Generation not found"
                                )
                        );


        return toGenerationResponse(
                generation
        );
    }


    // ============================================================
    // GET PROJECT GENERATIONS
    // ============================================================

    public List<GenerationResponse> getProjectGenerations(
            String projectId,
            String userId
    ) {

        projectRepository
                .findByIdAndUserId(
                        projectId,
                        userId
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Project not found"
                        )
                );


        return generationRepository
                .findByProjectId(projectId)
                .stream()
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

        projectRepository
                .findByIdAndUserId(
                        projectId,
                        userId
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Project not found"
                        )
                );


        Generation generation =
                generationRepository
                        .findByIdAndProjectId(
                                generationId,
                                projectId
                        )
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Generation not found"
                                )
                        );


        GenerationStatus newStatus;

        try {

            newStatus =
                    GenerationStatus.valueOf(
                            request.getStatus()
                                    .toUpperCase()
                    );

        } catch (Exception e) {

            throw new IllegalArgumentException(
                    "Invalid generation status: "
                            + request.getStatus()
            );
        }


        generation.setStatus(
                newStatus
        );


        if (newStatus == GenerationStatus.COMPLETED
                || newStatus == GenerationStatus.FAILED) {

            generation.setCompletedAt(
                    LocalDateTime.now()
            );

        } else {

            generation.setCompletedAt(null);
        }


        Generation updatedGeneration =
                generationRepository.save(
                        generation
                );


        return toGenerationResponse(
                updatedGeneration
        );
    }


    // ============================================================
    // START GENERATION
    // ============================================================

    public GenerationResponse startGeneration(
            String projectId,
            String generationId,
            String userId
    ) {

        projectRepository
                .findByIdAndUserId(
                        projectId,
                        userId
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Project not found"
                        )
                );


        Generation generation =
                generationRepository
                        .findByIdAndProjectId(
                                generationId,
                                projectId
                        )
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Generation not found"
                                )
                        );


        if (generation.getStatus()
                != GenerationStatus.PENDING) {

            throw new IllegalStateException(
                    "Generation can only be started when status is PENDING"
            );
        }


        // --------------------------------------------------------
        // Immediately mark as IN_PROGRESS
        // --------------------------------------------------------

        generation.setStatus(
                GenerationStatus.IN_PROGRESS
        );

        generation.setCompletedAt(null);


        Generation updatedGeneration =
                generationRepository.save(
                        generation
                );


        // --------------------------------------------------------
        // Start background worker
        // --------------------------------------------------------

        generationWorkerService.processGeneration(
                projectId,
                generationId,
                userId
        );


        return toGenerationResponse(
                updatedGeneration
        );
    }


    // ============================================================
    // ENTITY -> RESPONSE
    // ============================================================

    private GenerationResponse toGenerationResponse(
            Generation generation
    ) {

        return new GenerationResponse(
                generation.getId(),
                generation.getPrompt(),
                generation.getStatus().name(),
                generation.getProject().getId(),
                generation.getCreatedAt(),
                generation.getCompletedAt()
        );
    }
}