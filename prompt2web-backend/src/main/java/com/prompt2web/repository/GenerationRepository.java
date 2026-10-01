package com.prompt2web.repository;

import com.prompt2web.entity.Generation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GenerationRepository extends JpaRepository<Generation, String> {

    List<Generation> findByProjectId(String projectId);

    Optional<Generation> findByIdAndProjectId(
            String id,
            String projectId
    );
}