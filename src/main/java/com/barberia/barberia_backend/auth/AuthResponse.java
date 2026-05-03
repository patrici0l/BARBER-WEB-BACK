package com.barberia.barberia_backend.auth;

import com.barberia.barberia_backend.common.enums.Role;

public record AuthResponse(
        String token,
        Long userId,
        String name,
        String email,
        Role role
) {
}