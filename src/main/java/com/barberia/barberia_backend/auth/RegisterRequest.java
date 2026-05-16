package com.barberia.barberia_backend.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String name,

        @Email(message = "El correo no es valido")
        @NotBlank(message = "El correo es obligatorio")
        String email,

        String phoneNumber,

        @Size(min = 6, message = "La contrasena debe tener minimo 6 caracteres")
        @NotBlank(message = "La contrasena es obligatoria")
        String password) {
}
