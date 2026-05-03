package com.barberia.barberia_backend.service;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BarberServiceRequest(

        @NotBlank(message = "El nombre del servicio es obligatorio")
        String name,

        String description,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
        BigDecimal price,

        @NotNull(message = "La duración es obligatoria")
        @Min(value = 5, message = "La duración mínima debe ser de 5 minutos")
        Integer durationMinutes
) {
}