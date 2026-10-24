package com.fixflow.ai.service;

import com.fixflow.ai.dto.auth.RegisterRequest;
import com.fixflow.ai.dto.auth.UserDto;
import com.fixflow.ai.exception.BadRequestException;
import com.fixflow.ai.model.User;
import com.fixflow.ai.model.enums.Role;
import com.fixflow.ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto register(RegisterRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new BadRequestException("Email already registered");
        }
        if (req.getRole() == null || req.getRole() == Role.ADMIN) {
            throw new BadRequestException("Invalid role");
        }
        User user = User.builder()
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .role(req.getRole())
                .enabled(true)
                .build();
        user = userRepository.save(user);
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    public UserDto me(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new com.fixflow.ai.exception.ResourceNotFoundException("User not found"));
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }
}
