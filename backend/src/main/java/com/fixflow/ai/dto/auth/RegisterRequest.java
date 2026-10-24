package com.fixflow.ai.dto.auth;

import com.fixflow.ai.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String fullName;
    @NotNull
    private Role role; // CUSTOMER or STAFF (ADMIN not allowed via register for safety)
}
