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


    // ============================================================
    // GENERIC AI GENERATION
    // ============================================================

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

        return extractContent(response);
    }


    // ============================================================
    // GENERATE COMPLETE WEBSITE
    // ============================================================

    public String generateWebsite(String userPrompt) {

        String systemPrompt = """
                You are an expert full-stack frontend engineer and AI website builder.

                Your job is to generate a COMPLETE, RUNNABLE frontend project
                from the user's website request.

                The generated project will be saved directly into a project
                directory and executed using:

                    npm install
                    npm run build

                Therefore every required file must be included.

                ============================================================
                OUTPUT FORMAT
                ============================================================

                Return ONLY valid JSON.

                Do NOT return markdown.

                Do NOT use:
                ```json

                Do NOT use:
                ```

                Do NOT add explanations before or after the JSON.

                The JSON structure MUST be:

                {
                  "files": [
                    {
                      "filePath": "package.json",
                      "fileName": "package.json",
                      "language": "json",
                      "content": "complete file content"
                    }
                  ]
                }

                ============================================================
                PROJECT REQUIREMENTS
                ============================================================

                1. Generate a React + Vite application.

                2. Generate a complete package.json.

                3. package.json MUST contain valid npm dependencies.

                4. package.json MUST contain:

                   "scripts": {
                     "dev": "vite",
                     "build": "vite build"
                   }

                5. Generate index.html.

                6. Generate src/main.jsx.

                7. Generate src/App.jsx.

                8. Generate all additional React components required by
                   the requested website.

                9. Generate CSS files when required.

                10. Tailwind CSS:

                    Use Tailwind CSS when it is useful for the requested
                    design.

                    If Tailwind CSS is used, generate ALL required files:

                    tailwind.config.js
                    postcss.config.js

                    and include the required dependencies in package.json.

                    Do not use Tailwind classes without configuring Tailwind.

                11. If Tailwind is NOT required, do not generate unnecessary
                    Tailwind configuration files.

                12. Use React Router only when routing is actually required.

                13. If React Router is used, include react-router-dom
                    in package.json.

                14. If lucide-react is used, include lucide-react
                    in package.json.

                15. Every imported npm package MUST exist in package.json.

                16. Do not import packages that are not declared in
                    package.json.

                17. Every imported local file must actually be generated.

                18. Do not reference files that do not exist.

                19. All files must contain COMPLETE code.

                20. Never write:

                    "rest of code"
                    "add your code here"
                    "implement this"
                    "..."
                    "TODO"

                21. The application must compile successfully with:

                    npm install
                    npm run build

                22. Do not generate backend code.

                23. Do not generate Node.js backend servers.

                24. Do not generate Express applications.

                25. Do not generate Prisma.

                26. Generate only the frontend project.

                27. Use modern React functional components.

                28. Create reusable components when appropriate.

                29. Make the UI visually complete and professional.

                30. Make the website responsive for desktop, tablet,
                    and mobile.

                31. Do not use external images that are likely to fail.
                    Prefer reliable image URLs or CSS-based visual elements.

                32. If an image is required, use a valid remote URL.

                33. Do not leave broken imports.

                34. filePath must be the real relative project path.

                35. fileName must contain only the filename.

                36. language must identify the file language.

                ============================================================
                REQUIRED BASIC FILES
                ============================================================

                At minimum generate:

                package.json
                index.html
                src/main.jsx
                src/App.jsx

                Add other files as required by the website.

                ============================================================
                PACKAGE.JSON RULE
                ============================================================

                The package.json generated by you is the project's
                authoritative package.json.

                Do not assume that the backend will add dependencies later.

                Include every npm package required by the generated source code.

                ============================================================
                QUALITY RULE
                ============================================================

                Before returning the JSON, internally verify:

                - package.json exists
                - index.html exists
                - src/main.jsx exists
                - src/App.jsx exists
                - every imported npm package exists in package.json
                - every local import points to a generated file
                - Tailwind configuration exists when Tailwind is used
                - postcss configuration exists when Tailwind is used
                - the project can run npm install
                - the project can run npm run build

                Return ONLY the JSON object.
                """;

        String completePrompt =
                systemPrompt
                        + "\n\nUSER WEBSITE REQUEST:\n"
                        + userPrompt;

        return generate(completePrompt);
    }


    // ============================================================
    // CODE EDIT GENERATION
    // ============================================================

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

        return extractContent(response);
    }


    // ============================================================
    // EXTRACT OPENROUTER CONTENT
    // ============================================================

    private String extractContent(String response) {

        if (response == null || response.isBlank()) {
            throw new RuntimeException(
                    "OpenRouter returned an empty response"
            );
        }

        try {

            JsonNode root = objectMapper.readTree(response);

            JsonNode choices = root.path("choices");

            if (!choices.isArray() || choices.isEmpty()) {
                throw new RuntimeException(
                        "OpenRouter response does not contain choices"
                );
            }

            JsonNode content =
                    choices.get(0)
                            .path("message")
                            .path("content");

            if (content.isMissingNode() || content.isNull()) {
                throw new RuntimeException(
                        "OpenRouter response does not contain message content"
                );
            }

            return content.asText();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to parse OpenRouter response",
                    e
            );
        }
    }
}