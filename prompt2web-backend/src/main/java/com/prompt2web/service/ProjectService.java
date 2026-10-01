package com.prompt2web.service;

import com.prompt2web.dto.ProjectResponse;
import com.prompt2web.entity.Project;
import com.prompt2web.entity.User;
import com.prompt2web.exception.ResourceNotFoundException;
import com.prompt2web.repository.ProjectRepository;
import com.prompt2web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectResponse createProject(
            String userId,
            String name,
            String initialPrompt,
            String framework
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Project project = Project.builder()
                .name(name)
                .initialPrompt(initialPrompt)
                .framework(framework)
                .user(user)
                .build();

        Project savedProject = projectRepository.save(project);

        return new ProjectResponse(
                savedProject.getId(),
                savedProject.getName(),
                savedProject.getInitialPrompt(),
                savedProject.getFramework(),
                savedProject.getCreatedAt(),
                savedProject.getUpdatedAt()
        );
    }


    public List<ProjectResponse> getUserProjects(String userId) {

        List<Project> projects = projectRepository.findByUserId(userId);

        return projects.stream()
                .map(project -> new ProjectResponse(
                        project.getId(),
                        project.getName(),
                        project.getInitialPrompt(),
                        project.getFramework(),
                        project.getCreatedAt(),
                        project.getUpdatedAt()
                ))
                .toList();
    }


    public ProjectResponse getProjectById(
            String projectId,
            String userId
    ) {

        Project project = projectRepository
                .findByIdAndUserId(projectId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getInitialPrompt(),
                project.getFramework(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }


    public ProjectResponse updateProject(
            String projectId,
            String userId,
            String name,
            String initialPrompt,
            String framework
    ) {

        Project project = projectRepository
                .findByIdAndUserId(projectId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        project.setName(name);
        project.setInitialPrompt(initialPrompt);
        project.setFramework(framework);

        Project updatedProject = projectRepository.save(project);

        return new ProjectResponse(
                updatedProject.getId(),
                updatedProject.getName(),
                updatedProject.getInitialPrompt(),
                updatedProject.getFramework(),
                updatedProject.getCreatedAt(),
                updatedProject.getUpdatedAt()
        );
    }


    public void deleteProject(
            String projectId,
            String userId
    ) {

        Project project = projectRepository
                .findByIdAndUserId(projectId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        projectRepository.delete(project);
    }






}