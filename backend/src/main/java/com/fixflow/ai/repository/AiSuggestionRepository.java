package com.fixflow.ai.repository;

import com.fixflow.ai.model.AiSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiSuggestionRepository extends JpaRepository<AiSuggestion, Long> {
    Optional<AiSuggestion> findByTicketId(Long ticketId);
}
