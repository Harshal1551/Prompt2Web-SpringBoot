package com.prompt2web.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenRouterService {

    private final ObjectMapper objectMapper;

    @Value("${openrouter.api.url}")
    private String apiUrl;

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.model}")
    private String model;

    public String generate(String prompt) {

        WebClient webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + apiKey
                )
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", new Object[]{
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                }
        );

        String response = webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {

            JsonNode root = objectMapper.readTree(response);

            return root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to parse OpenRouter response",
                    e
            );
        }
    }


    public String generateWebsite(String userPrompt) {

        String systemPrompt = """
            You are an expert web developer and AI website generator.

            The user will describe a website they want.

            Generate a complete frontend website based on the user's request.

            Return ONLY valid JSON.

            Do not use markdown.
            Do not use ```json.
            Do not add explanations outside the JSON.

            The JSON must have exactly this structure:

            {
              "files": [
                {
                  "filePath": "src/App.jsx",
                  "fileName": "App.jsx",
                  "language": "javascript",
                  "content": "complete file content"
                }
              ]
            }

            Rules:
            1. Generate a React + Vite website.
            2. Generate all important files required for the website.
            3. Each file must have complete working code.
            4. Use reusable React components when appropriate.
            5. Use modern and clean UI.
            6. Use Tailwind CSS when appropriate.
            7. Do not omit important code with comments such as "rest of code".
            8. Every file must contain its complete content.
            9. filePath must represent the actual project path.
            10. fileName must contain only the file name.
            11. language must describe the programming language.
            """;

        String completePrompt = systemPrompt
                + "\n\nUSER WEBSITE REQUEST:\n"
                + userPrompt;

        return generate(completePrompt);
    }


    public String generateCodeEdit(
            String systemPrompt,
            String userPrompt
    ) {

        WebClient webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + apiKey
                )
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", new Object[]{
                        Map.of(
                                "role", "system",
                                "content", systemPrompt
                        ),
                        Map.of(
                                "role", "user",
                                "content", userPrompt
                        )
                }
        );

        String response = webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {

            JsonNode root =
                    objectMapper.readTree(response);

            return root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to parse OpenRouter code response",
                    e
            );
        }
    }





}