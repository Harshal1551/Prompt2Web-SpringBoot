package com.prompt2web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prompt2web.dto.GeneratedFileResponse;
import com.prompt2web.dto.GeneratedProjectResponse;
import com.prompt2web.entity.Project;
import com.prompt2web.entity.ProjectFile;
import com.prompt2web.exception.ResourceNotFoundException;
import com.prompt2web.repository.ProjectFileRepository;
import com.prompt2web.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneratedFileService {

    private final ObjectMapper objectMapper;
    private final ProjectRepository projectRepository;
    private final ProjectFileRepository projectFileRepository;


    // ============================================================
    // SAVE GENERATED FILES
    // ============================================================

    public List<ProjectFile> saveGeneratedFiles(
            String projectId,
            String userId,
            String aiResponse
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

        try {

            // ----------------------------------------------------
            // Clean AI response
            // ----------------------------------------------------

            String cleanResponse =
                    cleanJsonResponse(aiResponse);


            // ----------------------------------------------------
            // Convert JSON to Java object
            // ----------------------------------------------------

            GeneratedProjectResponse generatedProject =
                    objectMapper.readValue(
                            cleanResponse,
                            GeneratedProjectResponse.class
                    );


            if (generatedProject == null
                    || generatedProject.getFiles() == null
                    || generatedProject.getFiles().isEmpty()) {

                throw new RuntimeException(
                        "AI did not generate any project files"
                );
            }


            // ----------------------------------------------------
            // Validate generated project
            // ----------------------------------------------------

            validateGeneratedProject(
                    generatedProject
            );


            // ----------------------------------------------------
            // Save files
            // ----------------------------------------------------

            List<ProjectFile> savedFiles =
                    new ArrayList<>();


            for (GeneratedFileResponse fileResponse :
                    generatedProject.getFiles()) {

                if (fileResponse.getFilePath() == null
                        || fileResponse.getFilePath().isBlank()) {

                    throw new RuntimeException(
                            "Generated file has empty filePath"
                    );
                }

                if (fileResponse.getContent() == null) {
                    fileResponse.setContent("");
                }


                // ------------------------------------------------
                // Existing file
                // ------------------------------------------------

                ProjectFile projectFile =
                        projectFileRepository
                                .findByProjectIdAndFilePath(
                                        projectId,
                                        fileResponse.getFilePath()
                                )
                                .orElse(null);


                if (projectFile != null) {

                    projectFile.setFileName(
                            fileResponse.getFileName()
                    );

                    projectFile.setLanguage(
                            fileResponse.getLanguage()
                    );

                    projectFile.setContent(
                            fileResponse.getContent()
                    );

                }

                // ------------------------------------------------
                // New file
                // ------------------------------------------------

                else {

                    projectFile =
                            ProjectFile.builder()
                                    .filePath(
                                            fileResponse.getFilePath()
                                    )
                                    .fileName(
                                            fileResponse.getFileName()
                                    )
                                    .language(
                                            fileResponse.getLanguage()
                                    )
                                    .content(
                                            fileResponse.getContent()
                                    )
                                    .project(project)
                                    .build();
                }


                ProjectFile savedFile =
                        projectFileRepository.save(
                                projectFile
                        );

                savedFiles.add(savedFile);
            }


            System.out.println(
                    "[Generation] Saved "
                            + savedFiles.size()
                            + " project files."
            );


            return savedFiles;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to parse and save generated files: "
                            + e.getMessage(),
                    e
            );
        }
    }


    // ============================================================
    // CLEAN JSON RESPONSE
    // ============================================================

    private String cleanJsonResponse(
            String aiResponse
    ) {

        if (aiResponse == null
                || aiResponse.isBlank()) {

            throw new RuntimeException(
                    "AI returned an empty response"
            );
        }

        String response =
                aiResponse.trim();


        // Remove ```json
        if (response.startsWith("```json")) {

            response =
                    response.substring(
                            7
                    ).trim();
        }

        // Remove ```
        else if (response.startsWith("```")) {

            response =
                    response.substring(
                            3
                    ).trim();
        }


        if (response.endsWith("```")) {

            response =
                    response.substring(
                            0,
                            response.length() - 3
                    ).trim();
        }


        return response;
    }


    // ============================================================
    // VALIDATE PROJECT
    // ============================================================

    private void validateGeneratedProject(
            GeneratedProjectResponse generatedProject
    ) {

        boolean packageJsonFound = false;
        boolean indexHtmlFound = false;
        boolean mainJsxFound = false;
        boolean appJsxFound = false;
        boolean viteConfigFound = false;

        for (GeneratedFileResponse file :
                generatedProject.getFiles()) {

            String path =
                    file.getFilePath();

            if (path == null) {
                continue;
            }

            String normalizedPath =
                    path.replace("\\", "/");


            if (normalizedPath.equals(
                    "package.json"
            )) {
                packageJsonFound = true;
            }

            if (normalizedPath.equals(
                    "index.html"
            )) {
                indexHtmlFound = true;
            }

            if (normalizedPath.equals(
                    "src/main.jsx"
            )) {
                mainJsxFound = true;
            }

            if (normalizedPath.equals(
                    "src/App.jsx"
            )) {
                appJsxFound = true;
            }

            if (normalizedPath.equals("vite.config.js")) {
                viteConfigFound = true;
            }
        }


        if (!packageJsonFound) {

            throw new RuntimeException(
                    "AI project is missing package.json"
            );
        }

        if (!indexHtmlFound) {

            throw new RuntimeException(
                    "AI project is missing index.html"
            );
        }

        if (!mainJsxFound) {

            throw new RuntimeException(
                    "AI project is missing src/main.jsx"
            );
        }

        if (!appJsxFound) {

            throw new RuntimeException(
                    "AI project is missing src/App.jsx"
            );
        }

        if (!viteConfigFound) {
            throw new RuntimeException(
                    "AI project is missing vite.config.js"
            );
        }

    }
}