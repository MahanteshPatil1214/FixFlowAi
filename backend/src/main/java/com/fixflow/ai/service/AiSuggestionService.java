package com.fixflow.ai.service;

import com.fixflow.ai.dto.ai.AiFeedbackRequest;
import com.fixflow.ai.dto.ai.AiSuggestionDto;
import com.fixflow.ai.dto.kb.KnowledgeArticleDto;
import com.fixflow.ai.exception.ForbiddenException;
import com.fixflow.ai.exception.ResourceNotFoundException;
import com.fixflow.ai.model.AiSuggestion;
import com.fixflow.ai.model.Ticket;
import com.fixflow.ai.model.User;
import com.fixflow.ai.model.enums.Role;
import com.fixflow.ai.repository.AiSuggestionRepository;
import com.fixflow.ai.repository.TicketRepository;
import com.fixflow.ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiSuggestionService {

    private final AiSuggestionRepository suggestionRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final GroqService groqService;
    private final KnowledgeService knowledgeService;

    @Transactional
    public AiSuggestionDto generate(Long ticketId, String requesterEmail) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        User requester = userRepository.findByEmail(requesterEmail).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (requester.getRole() == Role.CUSTOMER && !ticket.getCreator().getId().equals(requester.getId())) {
            throw new ForbiddenException("Not authorized");
        }
        List<KnowledgeArticleDto> articles = knowledgeService.searchRelevant(ticket.getTitle() + " " + ticket.getDescription());
        GroqService.AiSuggestionResult res = groqService.suggest(ticket.getTitle(), ticket.getDescription(), articles);
        String titles = articles.stream().map(KnowledgeArticleDto::getTitle).collect(Collectors.joining("; "));
        String excerpts = articles.stream().map(a -> a.getTitle() + ": " + truncate(a.getContent(), 200)).collect(Collectors.joining(" | "));
        AiSuggestion suggestion = suggestionRepository.findByTicketId(ticketId).orElse(AiSuggestion.builder().ticket(ticket).build());
        if (!res.isSuccess() && !res.isAbstain()) {
            // store minimal info about failure but don't auto-change ticket
            suggestion.setSuggestedCategory(ticket.getCategory().name());
            suggestion.setSuggestedPriority(ticket.getPriority().name());
            suggestion.setExplanation("AI suggestion unavailable: " + res.getError());
            suggestion.setSuggestedResponse("");
            suggestion.setStatus(AiSuggestion.AiSuggestionStatus.PENDING);
        } else {
            suggestion.setSuggestedCategory(res.getSuggestedCategory());
            suggestion.setSuggestedPriority(res.getSuggestedPriority());
            suggestion.setExplanation(res.getExplanation());
            suggestion.setSuggestedResponse(res.getSuggestedResponse());
            suggestion.setStatus(AiSuggestion.AiSuggestionStatus.PENDING);
        }
        suggestion.setMatchingArticleTitles(titles);
        suggestion.setMatchingArticleExcerpts(excerpts);
        suggestion = suggestionRepository.save(suggestion);
        return toDto(suggestion);
    }

    public AiSuggestionDto get(Long ticketId, String requesterEmail) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        User requester = userRepository.findByEmail(requesterEmail).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (requester.getRole() == Role.CUSTOMER && !ticket.getCreator().getId().equals(requester.getId())) {
            throw new ForbiddenException("Not authorized");
        }
        AiSuggestion suggestion = suggestionRepository.findByTicketId(ticketId).orElseThrow(() -> new ResourceNotFoundException("No suggestion yet"));
        return toDto(suggestion);
    }

    @Transactional
    public AiSuggestionDto review(Long ticketId, AiFeedbackRequest req, String requesterEmail) {
        User reviewer = userRepository.findByEmail(requesterEmail).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (reviewer.getRole() == Role.CUSTOMER) {
            throw new ForbiddenException("Only staff can review AI suggestions");
        }
        AiSuggestion suggestion = suggestionRepository.findByTicketId(ticketId).orElseThrow(() -> new ResourceNotFoundException("No suggestion yet"));
        if (req.getAction() == null) {
            throw new com.fixflow.ai.exception.BadRequestException("Action required");
        }
        String action = req.getAction().toUpperCase();
        if (action.equals("ACCEPT")) {
            suggestion.setStatus(AiSuggestion.AiSuggestionStatus.ACCEPTED);
        } else if (action.equals("REJECT")) {
            suggestion.setStatus(AiSuggestion.AiSuggestionStatus.REJECTED);
        } else if (action.equals("EDIT")) {
            suggestion.setStatus(AiSuggestion.AiSuggestionStatus.EDITED);
            if (req.getEditedResponse() != null) {
                suggestion.setSuggestedResponse(req.getEditedResponse());
            }
        } else {
            throw new com.fixflow.ai.exception.BadRequestException("Invalid action");
        }
        suggestion.setFeedback(req.getFeedback());
        suggestion.setReviewedBy(reviewer.getFullName());
        suggestion.setReviewedAt(Instant.now());
        suggestion = suggestionRepository.save(suggestion);
        return toDto(suggestion);
    }

    private AiSuggestionDto toDto(AiSuggestion s) {
        return AiSuggestionDto.builder()
                .id(s.getId())
                .ticketId(s.getTicket() != null ? s.getTicket().getId() : null)
                .suggestedCategory(s.getSuggestedCategory())
                .suggestedPriority(s.getSuggestedPriority())
                .explanation(s.getExplanation())
                .suggestedResponse(s.getSuggestedResponse())
                .matchingArticleTitles(s.getMatchingArticleTitles())
                .matchingArticleExcerpts(s.getMatchingArticleExcerpts())
                .feedback(s.getFeedback())
                .status(s.getStatus() != null ? s.getStatus().name() : null)
                .reviewedBy(s.getReviewedBy())
                .reviewedAt(s.getReviewedAt())
                .createdAt(s.getCreatedAt())
                .build();
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max) + "...";
    }
}
