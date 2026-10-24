package com.fixflow.ai.dto.auth;

import com.fixflow.ai.model.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Long id;
    private String email;
    private String fullName;
    private Role role;
}
