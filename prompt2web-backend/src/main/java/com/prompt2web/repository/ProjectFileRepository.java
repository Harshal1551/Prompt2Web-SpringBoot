package com.prompt2web.repository;

import com.prompt2web.entity.ProjectFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectFileRepository extends JpaRepository<ProjectFile, String> {

    List<ProjectFile> findByProjectId(String projectId);

    Optional<ProjectFile> findByProjectIdAndFilePath(
            String projectId,
            String filePath
    );
}