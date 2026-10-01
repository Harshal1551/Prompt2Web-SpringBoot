package com.prompt2web.repository;

import com.prompt2web.entity.AiChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiChatMessageRepository
        extends JpaRepository<AiChatMessage, String> {

    List<AiChatMessage> findByProjectIdOrderByCreatedAtAsc(
            String projectId
    );
}