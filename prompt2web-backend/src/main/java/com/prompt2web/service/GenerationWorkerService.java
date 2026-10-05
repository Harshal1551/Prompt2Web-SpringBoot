package com.prompt2web.service;

import com.prompt2web.entity.Generation;
import com.prompt2web.entity.GenerationStatus;
import com.prompt2web.repository.GenerationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GenerationWorkerService {

    private final GenerationRepository generationRepository;
    private final OpenRouterService openRouterService;
    private final GeneratedFileService generatedFileService;
    private final PreviewService previewService;


    // ============================================================
    // PROCESS GENERATION
    // ============================================================

    @Async
    public void processGeneration(
            String projectId,
            String generationId,
            String userId
    ) {

        Generation generation =
                generationRepository
                        .findByIdAndProjectId(
                                generationId,
                                projectId
                        )
                        .orElse(null);


        if (generation == null) {

            System.err.println(
                    "[Generation] Generation not found: "
                            + generationId
            );

            return;
        }


        try {

            System.out.println(
                    "[Generation] AI generation started: "
                            + generationId
            );


            // ====================================================
            // 1. AI GENERATION
            // ====================================================

            String aiResponse =
                    openRouterService.generateWebsite(
                            generation.getPrompt()
                    );

            System.out.println(
                    "[Generation] AI RAW RESPONSE:\n" + aiResponse
            );


            System.out.println(
                    "[Generation] AI response received: "
                            + generationId
            );


            // ====================================================
            // 2. SAVE GENERATED FILES
            // ====================================================

            generatedFileService.saveGeneratedFiles(
                    projectId,
                    userId,
                    aiResponse
            );


            System.out.println(
                    "[Generation] Generated files saved: "
                            + generationId
            );


            // ====================================================
            // 3. GENERATION COMPLETED
            // ====================================================

            generation.setStatus(
                    GenerationStatus.COMPLETED
            );

            generation.setCompletedAt(
                    LocalDateTime.now()
            );

            generationRepository.save(
                    generation
            );


            System.out.println(
                    "[Generation] Generation completed: "
                            + generationId
            );


            // ====================================================
            // 4. BUILD PREVIEW
            // ====================================================

            try {

                System.out.println(
                        "[Generation] Starting preview build..."
                );

                previewService.buildPreview(
                        projectId,
                        userId
                );

                System.out.println(
                        "[Generation] Preview build completed."
                );

            } catch (Exception previewException) {

                /*
                 * Generation itself is already completed.
                 * Preview failure should be logged separately.
                 */

                System.err.println(
                        "[Generation] Preview build failed: "
                                + previewException.getMessage()
                );

                previewException.printStackTrace();
            }


        } catch (Exception e) {

            System.err.println(
                    "[Generation] Generation failed: "
                            + generationId
            );

            e.printStackTrace();


            generation.setStatus(
                    GenerationStatus.FAILED
            );

            generation.setCompletedAt(
                    LocalDateTime.now()
            );

            generationRepository.save(
                    generation
            );
        }
    }
}