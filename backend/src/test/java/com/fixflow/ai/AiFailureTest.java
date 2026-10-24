package com.fixflow.ai;

import com.fixflow.ai.dto.ai.AiSuggestionDto;
import com.fixflow.ai.model.AiSuggestion;
import com.fixflow.ai.model.Ticket;
import com.fixflow.ai.model.User;
import com.fixflow.ai.model.enums.Role;
import com.fixflow.ai.model.enums.TicketCategory;
import com.fixflow.ai.model.enums.TicketPriority;
import com.fixflow.ai.model.enums.TicketStatus;
import com.fixflow.ai.repository.AiSuggestionRepository;
import com.fixflow.ai.repository.TicketRepository;
import com.fixflow.ai.repository.UserRepository;
import com.fixflow.ai.service.AiSuggestionService;
import com.fixflow.ai.service.GroqService;
import com.fixflow.ai.service.KnowledgeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AiFailureTest {

    @Mock
    private AiSuggestionRepository suggestionRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GroqService groqService;
    @Mock
    private KnowledgeService knowledgeService;

    @InjectMocks
    private AiSuggestionService aiSuggestionService;

    @Test
    void aiUnavailableStillCreatesSuggestionRecord() {
        User staff = User.builder().id(2L).email("s@x.com").role(Role.STAFF).build();
        Ticket t = Ticket.builder().id(5L).title("t").description("d").category(TicketCategory.OTHER)
                .priority(TicketPriority.MEDIUM).status(TicketStatus.OPEN).creator(User.builder().id(1L).build()).build();
        when(ticketRepository.findById(5L)).thenReturn(Optional.of(t));
        when(userRepository.findByEmail("s@x.com")).thenReturn(Optional.of(staff));
        when(knowledgeService.searchRelevant(anyString())).thenReturn(Collections.emptyList());
        when(groqService.suggest(anyString(), anyString(), anyList())).thenReturn(GroqService.AiSuggestionResult.unavailable());
        when(suggestionRepository.findByTicketId(5L)).thenReturn(Optional.empty());
        when(suggestionRepository.save(any(AiSuggestion.class))).thenAnswer(inv -> {
            AiSuggestion s = inv.getArgument(0);
            s.setId(99L);
            return s;
        });
        AiSuggestionDto dto = aiSuggestionService.generate(5L, "s@x.com");
        assertEquals("unavailable", dto.getExplanation().contains("unavailable") ? "unavailable" : "other");
    }
}
