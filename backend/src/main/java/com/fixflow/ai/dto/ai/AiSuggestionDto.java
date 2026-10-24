package com.fixflow.ai.dto.ai;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AiSuggestionDto {
    private Long id;
    private Long ticketId;
    private String suggestedCategory;
    private String suggestedPriority;
    private String explanation;
    private String suggestedResponse;
    private String matchingArticleTitles;
    private String matchingArticleExcerpts;
    private String feedback;
    private String status;
    private String reviewedBy;
    private Instant reviewedAt;
    private Instant createdAt;
}
