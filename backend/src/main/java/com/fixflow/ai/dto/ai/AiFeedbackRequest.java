package com.fixflow.ai.dto.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AiFeedbackRequest {
    @NotNull
    private String action; // ACCEPT, REJECT, EDIT
    private String feedback;
    private String editedResponse;
}
