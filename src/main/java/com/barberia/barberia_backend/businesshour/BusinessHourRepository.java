package com.barberia.barberia_backend.businesshour;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.Optional;

public interface BusinessHourRepository extends JpaRepository<BusinessHour, Long> {
    Optional<BusinessHour> findByDayOfWeek(DayOfWeek dayOfWeek);
    boolean existsByDayOfWeek(DayOfWeek dayOfWeek);
}