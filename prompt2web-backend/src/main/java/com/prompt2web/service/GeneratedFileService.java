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


    public List<ProjectFile> saveGeneratedFiles(
            String projectId,
            String userId,
            String aiResponse
    ) {

        // 1. Verify project belongs to logged-in user
        Project project = projectRepository
                .findByIdAndUserId(projectId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));


        try {

            // 2. Convert AI JSON into Java object
            GeneratedProjectResponse generatedProject =
                    objectMapper.readValue(
                            aiResponse,
                            GeneratedProjectResponse.class
                    );


            // 3. Store generated/updated files
            List<ProjectFile> savedFiles =
                    new ArrayList<>();


            for (GeneratedFileResponse fileResponse :
                    generatedProject.getFiles()) {

                // 4. Check whether file already exists
                ProjectFile projectFile =
                        projectFileRepository
                                .findByProjectIdAndFilePath(
                                        projectId,
                                        fileResponse.getFilePath()
                                )
                                .orElse(null);


                // 5. Existing file -> UPDATE
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

                // 6. New file -> CREATE
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


                // 7. Save file
                ProjectFile savedFile =
                        projectFileRepository.save(projectFile);

                savedFiles.add(savedFile);
            }


            // 8. Return saved/updated files
            return savedFiles;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to parse and save generated files",
                    e
            );
        }
    }
}