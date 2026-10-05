package com.prompt2web.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prompt2web.entity.ProjectFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiCodeRepairService {

    private final ObjectMapper objectMapper;

    @Value("${openrouter.api.url}")
    private String openRouterApiUrl;

    @Value("${openrouter.api.key}")
    private String openRouterApiKey;

    @Value("${openrouter.model}")
    private String openRouterModel;

    // REPAIR BUILD ERROR
    public RepairResult repairBuildError(
            String buildError,
            List<ProjectFile> projectFiles
    ) throws Exception {

        String projectContext = buildProjectContext(projectFiles);

        String prompt = """
                You are an expert React, Vite and JavaScript debugging AI.

                A generated React project failed during production build.

                Your job is to identify the exact broken file and return corrected code.

                BUILD ERROR:
                %s

                PROJECT FILES:
                %s

                IMPORTANT RULES:

                1. Find the exact file causing the build error.
                2. Fix only the necessary problem.
                3. Do not redesign the application.
                4. Do not remove existing functionality.
                5. Do not change unrelated files.
                6. Return the COMPLETE corrected content of the broken file.
                7. The corrected code must be valid React/JavaScript.
                8. Do not use markdown code fences.
                9. Do not add explanations inside correctedCode.
                10. Return ONLY valid JSON.

                Required JSON format:

                {
                  "filePath": "src/pages/Example.jsx",
                  "correctedCode": "complete corrected file content",
                  "explanation": "short explanation of what was fixed"
                }
                """.formatted(
                buildError,
                projectContext
        );

        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put("model", openRouterModel);

        List<Map<String, String>> messages = new ArrayList<>();

        messages.add(Map.of(
                "role",
                "system",
                "content",
                "You are a senior React and Vite debugging engineer. Return only valid JSON."
        ));

        messages.add(Map.of(
                "role",
                "user",
                "content",
                prompt
        ));

        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.1);

        RestClient restClient = RestClient.builder()
                .baseUrl(openRouterApiUrl)
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + openRouterApiKey
                )
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();

        String response = restClient
                .post()
                .body(requestBody)
                .retrieve()
                .body(String.class);

        if (response == null || response.isBlank()) {
            throw new RuntimeException(
                    "OpenRouter returned an empty response"
            );
        }

        return parseRepairResponse(response);
    }

    // BUILD PROJECT CONTEXT
    private String buildProjectContext(
            List<ProjectFile> projectFiles
    ) {

        StringBuilder context = new StringBuilder();

        for (ProjectFile file : projectFiles) {

            context.append("\n");
            context.append("===== FILE: ");
            context.append(file.getFilePath());
            context.append(" =====\n");

            context.append(
                    file.getContent() == null
                            ? ""
                            : file.getContent()
            );

            context.append("\n");
            context.append("===== END FILE =====\n");
        }

        return context.toString();
    }

    // PARSE AI RESPONSE
    private RepairResult parseRepairResponse(
            String response
    ) throws Exception {

        JsonNode root = objectMapper.readTree(response);

        JsonNode choices = root.get("choices");

        if (choices == null
                || !choices.isArray()
                || choices.isEmpty()) {

            throw new RuntimeException(
                    "OpenRouter response does not contain choices"
            );
        }

        JsonNode message =
                choices.get(0).get("message");

        if (message == null) {

            throw new RuntimeException(
                    "OpenRouter response does not contain message"
            );
        }

        String content =
                message.get("content").asText();

        if (content == null || content.isBlank()) {

            throw new RuntimeException(
                    "OpenRouter returned empty AI content"
            );
        }

        content = cleanJsonResponse(content);

        JsonNode repair =
                objectMapper.readTree(content);

        JsonNode filePathNode =
                repair.get("filePath");

        JsonNode correctedCodeNode =
                repair.get("correctedCode");

        JsonNode explanationNode =
                repair.get("explanation");

        if (filePathNode == null
                || correctedCodeNode == null) {

            throw new RuntimeException(
                    "OpenRouter returned invalid repair JSON"
            );
        }

        String filePath =
                filePathNode.asText();

        String correctedCode =
                correctedCodeNode.asText();

        String explanation =
                explanationNode == null
                        ? ""
                        : explanationNode.asText();

        if (filePath.isBlank()) {

            throw new RuntimeException(
                    "AI did not provide a file path"
            );
        }

        if (correctedCode.isBlank()) {

            throw new RuntimeException(
                    "AI did not provide corrected code"
            );
        }

        return new RepairResult(
                filePath,
                correctedCode,
                explanation
        );
    }

    // CLEAN JSON RESPONSE
    private String cleanJsonResponse(
            String content
    ) {

        content = content.trim();

        if (content.startsWith("```json")) {

            content = content.substring(7);

        } else if (content.startsWith("```")) {

            content = content.substring(3);
        }

        if (content.endsWith("```")) {

            content = content.substring(
                    0,
                    content.length() - 3
            );
        }

        return content.trim();
    }

    // REPAIR RESULT
    public record RepairResult(
            String filePath,
            String correctedCode,
            String explanation
    ) {
    }
}