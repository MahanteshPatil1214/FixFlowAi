package com.fixflow.ai;

import com.fixflow.ai.dto.ticket.TicketCreateRequest;
import com.fixflow.ai.exception.ForbiddenException;
import com.fixflow.ai.model.User;
import com.fixflow.ai.model.enums.Role;
import com.fixflow.ai.model.enums.TicketCategory;
import com.fixflow.ai.model.enums.TicketPriority;
import com.fixflow.ai.repository.TicketRepository;
import com.fixflow.ai.repository.TicketStatusHistoryRepository;
import com.fixflow.ai.repository.UserRepository;
import com.fixflow.ai.service.TicketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private TicketStatusHistoryRepository historyRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void createTicketSuccess() {
        User user = User.builder().id(1L).email("c@x.com").fullName("C").role(Role.CUSTOMER).build();
        when(userRepository.findByEmail("c@x.com")).thenReturn(Optional.of(user));
        when(ticketRepository.save(any())).thenAnswer(inv -> {
            com.fixflow.ai.model.Ticket t = inv.getArgument(0);
            t.setId(10L);
            return t;
        });
        when(historyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        TicketCreateRequest req = new TicketCreateRequest();
        req.setTitle("Test");
        req.setDescription("Desc");
        req.setCategory(TicketCategory.BILLING);
        req.setPriority(TicketPriority.MEDIUM);
        var dto = ticketService.create(req, "c@x.com");
        assertNotNull(dto.getId());
        assertEquals("Test", dto.getTitle());
    }
}
