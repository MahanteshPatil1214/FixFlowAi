package com.fixflow.ai.controller;

import com.fixflow.ai.dto.ai.AiFeedbackRequest;
import com.fixflow.ai.dto.ai.AiSuggestionDto;
import com.fixflow.ai.service.AiSuggestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiSuggestionService aiSuggestionService;

    @PostMapping("/tickets/{ticketId}/suggest")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<AiSuggestionDto> suggest(@PathVariable Long ticketId, Authentication auth) {
        return ResponseEntity.ok(aiSuggestionService.generate(ticketId, auth.getName()));
    }

    @GetMapping("/tickets/{ticketId}/suggest")
    @PreAuthorize("hasAnyRole('CUSTOMER','STAFF','ADMIN')")
    public ResponseEntity<AiSuggestionDto> get(@PathVariable Long ticketId, Authentication auth) {
        return ResponseEntity.ok(aiSuggestionService.get(ticketId, auth.getName()));
    }

    @PostMapping("/tickets/{ticketId}/review")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<AiSuggestionDto> review(@PathVariable Long ticketId, @Valid @RequestBody AiFeedbackRequest req, Authentication auth) {
        return ResponseEntity.ok(aiSuggestionService.review(ticketId, req, auth.getName()));
    }
}
