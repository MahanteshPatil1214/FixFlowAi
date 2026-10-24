package com.fixflow.ai;

import com.fixflow.ai.dto.ticket.TicketUpdateRequest;
import com.fixflow.ai.exception.ForbiddenException;
import com.fixflow.ai.model.Ticket;
import com.fixflow.ai.model.User;
import com.fixflow.ai.model.enums.Role;
import com.fixflow.ai.model.enums.TicketCategory;
import com.fixflow.ai.model.enums.TicketPriority;
import com.fixflow.ai.model.enums.TicketStatus;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketStatusTransitionTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private TicketStatusHistoryRepository historyRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void customerCannotChangeStatus() {
        User customer = User.builder().id(1L).email("c@x.com").role(Role.CUSTOMER).build();
        Ticket t = Ticket.builder().id(5L).title("t").description("d").category(TicketCategory.OTHER)
                .priority(TicketPriority.MEDIUM).status(TicketStatus.OPEN).creator(customer).build();
        when(ticketRepository.findById(5L)).thenReturn(Optional.of(t));
        when(userRepository.findByEmail("c@x.com")).thenReturn(Optional.of(customer));
        TicketUpdateRequest req = new TicketUpdateRequest();
        req.setStatus(TicketStatus.IN_PROGRESS);
        assertThrows(ForbiddenException.class, () -> ticketService.update(5L, req, "c@x.com"));
    }

    @Test
    void staffCanChangeStatus() {
        User staff = User.builder().id(2L).email("s@x.com").role(Role.STAFF).build();
        User creator = User.builder().id(1L).email("c@x.com").role(Role.CUSTOMER).build();
        Ticket t = Ticket.builder().id(5L).title("t").description("d").category(TicketCategory.OTHER)
                .priority(TicketPriority.MEDIUM).status(TicketStatus.OPEN).creator(creator).build();
        when(ticketRepository.findById(5L)).thenReturn(Optional.of(t));
        when(userRepository.findByEmail("s@x.com")).thenReturn(Optional.of(staff));
        when(ticketRepository.save(any())).thenReturn(t);
        when(historyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        TicketUpdateRequest req = new TicketUpdateRequest();
        req.setStatus(TicketStatus.IN_PROGRESS);
        assertDoesNotThrow(() -> ticketService.update(5L, req, "s@x.com"));
    }
}
