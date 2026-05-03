package com.barberia.barberia_backend.availability;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @GetMapping
    public List<AvailabilitySlotResponse> getAvailability(
            @RequestParam LocalDate date,
            @RequestParam Long serviceId) {
        return availabilityService.getAvailability(date, serviceId);
    }
}