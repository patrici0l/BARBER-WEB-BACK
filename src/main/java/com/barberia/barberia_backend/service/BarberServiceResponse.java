package com.barberia.barberia_backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BarberServiceResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer durationMinutes,
        Boolean active,
        LocalDateTime createdAt
) {
    public static BarberServiceResponse fromEntity(BarberService service) {
        return new BarberServiceResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getPrice(),
                service.getDurationMinutes(),
                service.getActive(),
                service.getCreatedAt()
        );
    }
}