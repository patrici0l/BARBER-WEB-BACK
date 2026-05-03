package com.barberia.barberia_backend.businesshour;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record BusinessHourResponse(
        Long id,
        DayOfWeek dayOfWeek,
        LocalTime openTime,
        LocalTime closeTime,
        Boolean active
) {
    public static BusinessHourResponse fromEntity(BusinessHour businessHour) {
        return new BusinessHourResponse(
                businessHour.getId(),
                businessHour.getDayOfWeek(),
                businessHour.getOpenTime(),
                businessHour.getCloseTime(),
                businessHour.getActive()
        );
    }
}