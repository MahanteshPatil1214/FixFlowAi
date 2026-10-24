package com.fixflow.ai.dto.ticket;

import com.fixflow.ai.model.enums.TicketCategory;
import com.fixflow.ai.model.enums.TicketPriority;
import com.fixflow.ai.model.enums.TicketStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TicketUpdateRequest {
    @Size(max = 200)
    private String title;
    @Size(max = 5000)
    private String description;
    private TicketCategory category;
    private TicketPriority priority;
    private TicketStatus status;
    private Long assigneeId;
}
