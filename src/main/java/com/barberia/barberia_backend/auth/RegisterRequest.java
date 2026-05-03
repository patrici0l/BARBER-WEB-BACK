package com.barberia.barberia_backend.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String name,

        @Email(message = "El correo no es válido")
        @NotBlank(message = "El correo es obligatorio")
        String email,

        @Size(min = 6, message = "La contraseña debe tener mínimo 6 caracteres")
        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {
}