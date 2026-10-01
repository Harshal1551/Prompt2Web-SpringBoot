package com.prompt2web.service;

import com.prompt2web.dto.AiChatRequest;
import com.prompt2web.dto.AiChatResponse;
import com.prompt2web.dto.AiChatHistoryResponse;
import com.prompt2web.entity.AiChatMessage;
import com.prompt2web.entity.Project;
import com.prompt2web.entity.ProjectFile;
import com.prompt2web.exception.ResourceNotFoundException;
import com.prompt2web.repository.AiChatMessageRepository;
import com.prompt2web.repository.ProjectFileRepository;
import com.prompt2web.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiChatService {

    private final ProjectRepository projectRepository;
    private final ProjectFileRepository projectFileRepository;
    private final AiChatMessageRepository aiChatMessageRepository;
    private final ProjectFileService projectFileService;
    private final OpenRouterService openRouterService;


    public AiChatResponse chat(
            String projectId,
            String userId,
            AiChatRequest request
    ) {

        // ============================================================
        // 1. VERIFY PROJECT
        // ============================================================

        Project project = projectRepository
                .findByIdAndUserId(projectId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found")
                );


        // ============================================================
        // 2. FIND SELECTED FILE
        // ============================================================

        ProjectFile file = null;

        if (request.getFileId() != null
                && !request.getFileId().isBlank()) {

            file = projectFileRepository
                    .findById(request.getFileId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("File not found")
                    );

            if (!file.getProject().getId().equals(projectId)) {

                throw new ResourceNotFoundException(
                        "File not found"
                );
            }

            projectFileService.getFile(
                    projectId,
                    request.getFileId(),
                    userId
            );
        }


        // ============================================================
        // 3. SAVE USER MESSAGE
        // ============================================================

        AiChatMessage userMessage =
                AiChatMessage.builder()
                        .message(request.getMessage())
                        .role("USER")
                        .fileId(
                                file != null
                                        ? file.getId()
                                        : null
                        )
                        .project(project)
                        .build();

        aiChatMessageRepository.save(userMessage);


        // ============================================================
        // 4. NORMAL AI CHAT
        // ============================================================

        if (file == null) {

            String aiResponse =
                    openRouterService.generate(
                            request.getMessage()
                    );

            AiChatMessage assistantMessage =
                    AiChatMessage.builder()
                            .message(aiResponse)
                            .role("ASSISTANT")
                            .fileId(null)
                            .project(project)
                            .build();

            aiChatMessageRepository.save(
                    assistantMessage
            );

            return new AiChatResponse(
                    aiResponse,
                    null,
                    null
            );
        }


        // ============================================================
        // 5. CODE EDIT SYSTEM PROMPT
        // ============================================================

        String systemPrompt = """
                You are a professional software engineer
                working inside Prompt2Web.

                Your job is to modify the user's selected source file.

                STRICT RULES:

                1. Return ONLY the complete source code.
                2. Never return explanations.
                3. Never return markdown.
                4. Never return ``` blocks.
                5. Never return safety messages.
                6. Never return text such as "User Safety: safe".
                7. Never say that you cannot modify the file.
                8. Never summarize the changes.
                9. Preserve all existing functionality unless
                   the user explicitly requests a change.
                10. Make the smallest necessary code changes.
                11. Return complete working code.
                """;


        // ============================================================
        // 6. USER PROMPT
        // ============================================================

        String userPrompt = """
                Modify this file according to the user's request.

                FILE PATH:
                %s

                FILE NAME:
                %s

                LANGUAGE:
                %s

                CURRENT FILE CONTENT:
                --------------------------------

                %s

                --------------------------------

                USER REQUEST:
                %s

                Return ONLY the complete updated file content.
                """.formatted(
                file.getFilePath(),
                file.getFileName(),
                file.getLanguage(),
                file.getContent(),
                request.getMessage()
        );


        // ============================================================
        // 7. ASK CODING MODEL
        // ============================================================

        String updatedContent =
                openRouterService.generateCodeEdit(
                        systemPrompt,
                        userPrompt
                );


        // ============================================================
        // 8. CLEAN MARKDOWN IF MODEL ADDS IT
        // ============================================================

        updatedContent =
                cleanAiResponse(updatedContent);


        // ============================================================
        // 9. BASIC RESPONSE VALIDATION
        // ============================================================

        validateAiCodeResponse(
                file,
                updatedContent
        );


        // ============================================================
        // 10. UPDATE EXISTING FILE
        // ============================================================

        file.setContent(updatedContent);

        ProjectFile updatedFile =
                projectFileRepository.save(file);


        // ============================================================
        // 11. SAVE ASSISTANT MESSAGE
        // ============================================================

        AiChatMessage assistantMessage =
                AiChatMessage.builder()
                        .message(
                                "File updated successfully"
                        )
                        .role("ASSISTANT")
                        .fileId(updatedFile.getId())
                        .project(project)
                        .build();

        aiChatMessageRepository.save(
                assistantMessage
        );


        // ============================================================
        // 12. RETURN
        // ============================================================

        return new AiChatResponse(
                "File updated successfully",
                updatedFile.getId(),
                updatedFile.getContent()
        );
    }


    // ============================================================
    // CLEAN AI RESPONSE
    // ============================================================

    private String cleanAiResponse(String content) {

        if (content == null) {
            throw new IllegalStateException(
                    "AI returned an empty response"
            );
        }

        String cleaned = content.trim();


        if (cleaned.startsWith("```")) {

            int firstNewLine =
                    cleaned.indexOf("\n");

            if (firstNewLine != -1) {

                cleaned =
                        cleaned.substring(
                                firstNewLine + 1
                        );
            }

            if (cleaned.endsWith("```")) {

                cleaned =
                        cleaned.substring(
                                0,
                                cleaned.length() - 3
                        );
            }
        }

        return cleaned.trim();
    }


    // ============================================================
    // VALIDATE AI RESPONSE
    // ============================================================

    private void validateAiCodeResponse(
            ProjectFile file,
            String content
    ) {

        if (content.isBlank()) {

            throw new IllegalStateException(
                    "AI returned empty file content"
            );
        }


        // Prevent obvious non-code responses
        String lowerContent =
                content.toLowerCase();


        if (lowerContent.equals(
                "user safety: safe"
        )) {

            throw new IllegalStateException(
                    "AI returned an invalid code response"
            );
        }


        String fileName =
                file.getFileName().toLowerCase();


        // JavaScript / JSX
        if (fileName.endsWith(".js")
                || fileName.endsWith(".jsx")
                || fileName.endsWith(".ts")
                || fileName.endsWith(".tsx")) {

            boolean looksLikeCode =
                    content.contains("import ")
                            || content.contains("export ")
                            || content.contains("function ")
                            || content.contains("const ")
                            || content.contains("return ")
                            || content.contains("<");

            if (!looksLikeCode) {

                throw new IllegalStateException(
                        "AI returned content that does not look like valid JavaScript/TypeScript code"
                );
            }
        }


        // HTML
        if (fileName.endsWith(".html")) {

            if (!content.contains("<")
                    || !content.contains(">")) {

                throw new IllegalStateException(
                        "AI returned invalid HTML content"
                );
            }
        }


        // CSS
        if (fileName.endsWith(".css")) {

            if (!content.contains("{")
                    || !content.contains("}")) {

                throw new IllegalStateException(
                        "AI returned invalid CSS content"
                );
            }
        }


        // JSON
        if (fileName.endsWith(".json")) {

            if (!(content.startsWith("{")
                    || content.startsWith("["))) {

                throw new IllegalStateException(
                        "AI returned invalid JSON content"
                );
            }
        }
    }

    // ============================================================
// GET CHAT HISTORY
// ============================================================

    public List<AiChatHistoryResponse> getChatHistory(
            String projectId,
            String userId
    ) {

        // Verify project belongs to logged-in user
        projectRepository
                .findByIdAndUserId(projectId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found")
                );

        // Get project chat history
        List<AiChatMessage> messages =
                aiChatMessageRepository
                        .findByProjectIdOrderByCreatedAtAsc(
                                projectId
                        );

        // Convert entity objects to response DTOs
        return messages.stream()
                .map(message -> new AiChatHistoryResponse(
                        message.getId(),
                        message.getRole(),
                        message.getMessage(),
                        message.getFileId(),
                        message.getCreatedAt()
                ))
                .toList();
    }


}