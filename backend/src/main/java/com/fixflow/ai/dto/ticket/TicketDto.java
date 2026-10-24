package com.fixflow.ai.dto.ticket;

import com.fixflow.ai.model.enums.TicketCategory;
import com.fixflow.ai.model.enums.TicketPriority;
import com.fixflow.ai.model.enums.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TicketDto {
    private Long id;
    private String title;
    private String description;
    private TicketCategory category;
    private TicketPriority priority;
    private TicketStatus status;
    private Long creatorId;
    private String creatorName;
    private Long assigneeId;
    private String assigneeName;
    private Instant createdAt;
    private Instant updatedAt;
}
