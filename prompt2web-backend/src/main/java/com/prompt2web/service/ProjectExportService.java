package com.prompt2web.service;

import com.prompt2web.entity.Project;
import com.prompt2web.entity.ProjectFile;
import com.prompt2web.exception.ResourceNotFoundException;
import com.prompt2web.repository.ProjectFileRepository;
import com.prompt2web.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class ProjectExportService {

    private final ProjectRepository projectRepository;
    private final ProjectFileRepository projectFileRepository;


    public byte[] exportProject(
            String projectId,
            String userId
    ) {

        // 1. Verify project belongs to logged-in user
        Project project = projectRepository
                .findByIdAndUserId(projectId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"
                        )
                );


        // 2. Get all project files
        List<ProjectFile> files =
                projectFileRepository.findByProjectId(
                        project.getId()
                );


        if (files.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No files found in project"
            );
        }


        try (
                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream();

                ZipOutputStream zipOutputStream =
                        new ZipOutputStream(
                                byteArrayOutputStream
                        )
        ) {

            // Keep track of paths already added to ZIP
            Set<String> addedPaths = new HashSet<>();


            // 3. Add every file to ZIP
            for (ProjectFile file : files) {

                String filePath = file.getFilePath();


                // Ignore invalid file paths
                if (filePath == null || filePath.isBlank()) {
                    continue;
                }


                // Normalize path
                filePath = filePath
                        .replace("\\", "/")
                        .replaceFirst("^/+", "");


                // Prevent duplicate ZIP entries
                if (!addedPaths.add(filePath)) {
                    continue;
                }


                // Create ZIP entry
                ZipEntry zipEntry =
                        new ZipEntry(filePath);

                zipOutputStream.putNextEntry(zipEntry);


                // Handle null content safely
                String content =
                        file.getContent() != null
                                ? file.getContent()
                                : "";


                byte[] contentBytes =
                        content.getBytes(
                                StandardCharsets.UTF_8
                        );


                zipOutputStream.write(contentBytes);

                zipOutputStream.closeEntry();
            }


            // 4. Finish ZIP
            zipOutputStream.finish();


            // 5. Return ZIP bytes
            return byteArrayOutputStream.toByteArray();


        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to create project ZIP",
                    e
            );
        }
    }
}