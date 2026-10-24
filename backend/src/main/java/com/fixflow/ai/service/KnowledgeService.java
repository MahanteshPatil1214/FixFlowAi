package com.fixflow.ai.service;

import com.fixflow.ai.dto.kb.KnowledgeArticleDto;
import com.fixflow.ai.model.KnowledgeArticle;
import com.fixflow.ai.repository.KnowledgeArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KnowledgeService {

    private final KnowledgeArticleRepository repo;

    public List<KnowledgeArticleDto> list() {
        return repo.findByApprovedTrue().stream()
                .map(a -> KnowledgeArticleDto.builder().id(a.getId()).title(a.getTitle()).content(a.getContent()).approved(a.isApproved()).build())
                .collect(Collectors.toList());
    }

    public List<KnowledgeArticleDto> searchRelevant(String text) {
        if (text == null || text.isBlank()) {
            return list().stream().limit(2).collect(Collectors.toList());
        }
        String lower = text.toLowerCase();
        Set<String> tokens = new HashSet<>(Arrays.asList(lower.replaceAll("[^a-z0-9 ]", " ").split("\\s+")));
        return repo.findByApprovedTrue().stream()
                .filter(a -> {
                    String combined = (a.getTitle() + " " + a.getContent()).toLowerCase();
                    return tokens.stream().anyMatch(t -> t.length() > 3 && combined.contains(t));
                })
                .limit(3)
                .map(a -> KnowledgeArticleDto.builder().id(a.getId()).title(a.getTitle()).content(a.getContent()).approved(a.isApproved()).build())
                .collect(Collectors.toList());
    }
}
