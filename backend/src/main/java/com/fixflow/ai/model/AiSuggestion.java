package com.fixflow.ai.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "ai_suggestions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiSuggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false, unique = true)
    private Ticket ticket;

    @Column(columnDefinition = "TEXT")
    private String suggestedCategory;

    @Column(columnDefinition = "TEXT")
    private String suggestedPriority;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(columnDefinition = "TEXT")
    private String suggestedResponse;

    @Column(columnDefinition = "TEXT")
    private String matchingArticleTitles;

    @Column(columnDefinition = "TEXT")
    private String matchingArticleExcerpts;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Enumerated(EnumType.STRING)
    private AiSuggestionStatus status;

    private String reviewedBy;
    private Instant reviewedAt;
    private Instant createdAt;

    public enum AiSuggestionStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
        EDITED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        if (status == null) {
            status = AiSuggestionStatus.PENDING;
        }
    }
}
