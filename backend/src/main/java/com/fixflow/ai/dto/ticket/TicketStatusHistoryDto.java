package com.fixflow.ai.dto.ticket;

import com.fixflow.ai.model.enums.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TicketStatusHistoryDto {
    private Long id;
    private TicketStatus fromStatus;
    private TicketStatus toStatus;
    private Long changedById;
    private String changedByName;
    private Instant changedAt;
}
