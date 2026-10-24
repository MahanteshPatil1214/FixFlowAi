package com.fixflow.ai.dto.ticket;

import com.fixflow.ai.model.enums.TicketCategory;
import com.fixflow.ai.model.enums.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TicketCreateRequest {
    @NotBlank
    @Size(max = 200)
    private String title;
    @NotBlank
    @Size(max = 5000)
    private String description;
    @NotNull
    private TicketCategory category;
    @NotNull
    private TicketPriority priority;
}
