package com.fixflow.ai.dto.kb;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KnowledgeArticleDto {
    private Long id;
    private String title;
    private String content;
    private boolean approved;
}
