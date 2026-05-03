package com.barberia.barberia_backend.availability;

import java.time.LocalTime;

public record AvailabilityResponse(
        LocalTime time,
        Boolean available
) {
}