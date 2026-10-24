package com.fixflow.ai;

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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketAuthorizationTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private TicketStatusHistoryRepository historyRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void customerCannotViewOthersTicket() {
        User customer = User.builder().id(1L).email("c@x.com").role(Role.CUSTOMER).build();
        User other = User.builder().id(2L).email("o@x.com").role(Role.CUSTOMER).build();
        Ticket t = Ticket.builder().id(5L).title("t").description("d").category(TicketCategory.OTHER)
                .priority(TicketPriority.MEDIUM).status(TicketStatus.OPEN).creator(other).build();
        when(ticketRepository.findById(5L)).thenReturn(Optional.of(t));
        when(userRepository.findByEmail("c@x.com")).thenReturn(Optional.of(customer));
        assertThrows(ForbiddenException.class, () -> ticketService.get(5L, "c@x.com"));
    }
}
