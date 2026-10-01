package com.prompt2web.service;

import com.prompt2web.dto.ProjectFileRequest;
import com.prompt2web.dto.ProjectFileResponse;
import com.prompt2web.entity.Project;
import com.prompt2web.entity.ProjectFile;
import com.prompt2web.exception.ResourceNotFoundException;
import com.prompt2web.repository.ProjectFileRepository;
import com.prompt2web.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectFileService {

    private final ProjectFileRepository projectFileRepository;
    private final ProjectRepository projectRepository;

    public ProjectFileResponse createFile(
            String projectId,
            String userId,
            ProjectFileRequest request
    ) {

        Project project = projectRepository
                .findByIdAndUserId(projectId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        ProjectFile projectFile = ProjectFile.builder()
                .filePath(request.getFilePath())
                .fileName(request.getFileName())
                .content(request.getContent())
                .language(request.getLanguage())
                .project(project)
                .build();

        ProjectFile savedFile =
                projectFileRepository.save(projectFile);

        return new ProjectFileResponse(
                savedFile.getId(),
                savedFile.getFilePath(),
                savedFile.getFileName(),
                savedFile.getContent(),
                savedFile.getLanguage(),
                savedFile.getCreatedAt(),
                savedFile.getUpdatedAt()
        );
    }


    public List<ProjectFileResponse> getProjectFiles(
            String projectId,
            String userId
    ) {

        projectRepository
                .findByIdAndUserId(projectId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        List<ProjectFile> files =
                projectFileRepository.findByProjectId(projectId);

        return files.stream()
                .map(file -> new ProjectFileResponse(
                        file.getId(),
                        file.getFilePath(),
                        file.getFileName(),
                        file.getContent(),
                        file.getLanguage(),
                        file.getCreatedAt(),
                        file.getUpdatedAt()
                ))
                .toList();
    }


    public ProjectFileResponse getFile(
            String projectId,
            String fileId,
            String userId
    ) {

        projectRepository
                .findByIdAndUserId(projectId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        ProjectFile file = projectFileRepository
                .findById(fileId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("File not found"));

        if (!file.getProject().getId().equals(projectId)) {
            throw new ResourceNotFoundException("File not found");
        }

        return new ProjectFileResponse(
                file.getId(),
                file.getFilePath(),
                file.getFileName(),
                file.getContent(),
                file.getLanguage(),
                file.getCreatedAt(),
                file.getUpdatedAt()
        );
    }


    public ProjectFileResponse updateFile(
            String projectId,
            String fileId,
            String userId,
            ProjectFileRequest request
    ) {

        projectRepository
                .findByIdAndUserId(projectId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        ProjectFile file = projectFileRepository
                .findById(fileId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("File not found"));

        if (!file.getProject().getId().equals(projectId)) {
            throw new ResourceNotFoundException("File not found");
        }

        file.setFilePath(request.getFilePath());
        file.setFileName(request.getFileName());
        file.setContent(request.getContent());
        file.setLanguage(request.getLanguage());

        ProjectFile updatedFile =
                projectFileRepository.save(file);

        return new ProjectFileResponse(
                updatedFile.getId(),
                updatedFile.getFilePath(),
                updatedFile.getFileName(),
                updatedFile.getContent(),
                updatedFile.getLanguage(),
                updatedFile.getCreatedAt(),
                updatedFile.getUpdatedAt()
        );
    }


    public void deleteFile(
            String projectId,
            String fileId,
            String userId
    ) {

        projectRepository
                .findByIdAndUserId(projectId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        ProjectFile file = projectFileRepository
                .findById(fileId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("File not found"));

        if (!file.getProject().getId().equals(projectId)) {
            throw new ResourceNotFoundException("File not found");
        }

        projectFileRepository.delete(file);
    }






}