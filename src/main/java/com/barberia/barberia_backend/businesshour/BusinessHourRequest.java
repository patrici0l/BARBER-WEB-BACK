package com.barberia.barberia_backend.businesshour;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record BusinessHourRequest(

        @NotNull(message = "El día de la semana es obligatorio")
        DayOfWeek dayOfWeek,

        LocalTime openTime,

        LocalTime closeTime,

        @NotNull(message = "El estado activo es obligatorio")
        Boolean active
) {
}