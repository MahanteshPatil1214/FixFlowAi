package com.fixflow.ai.controller;

import com.fixflow.ai.dto.kb.KnowledgeArticleDto;
import com.fixflow.ai.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/kb")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','STAFF','ADMIN')")
    public ResponseEntity<List<KnowledgeArticleDto>> list() {
        return ResponseEntity.ok(knowledgeService.list());
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<List<KnowledgeArticleDto>> search(@RequestParam String q) {
        return ResponseEntity.ok(knowledgeService.searchRelevant(q));
    }
}
