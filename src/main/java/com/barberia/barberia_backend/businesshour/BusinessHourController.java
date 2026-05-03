package com.barberia.barberia_backend.businesshour;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BusinessHourController {

    private final BusinessHourService businessHourService;

    @GetMapping("/business-hours")
    public List<BusinessHourResponse> getBusinessHours() {
        return businessHourService.findAll();
    }

    @GetMapping("/business-hours/{dayOfWeek}")
    public BusinessHourResponse getBusinessHourByDay(
            @PathVariable DayOfWeek dayOfWeek
    ) {
        return businessHourService.findByDay(dayOfWeek);
    }

    @GetMapping("/admin/business-hours")
    public List<BusinessHourResponse> getBusinessHoursForAdmin() {
        return businessHourService.findAll();
    }

    @PostMapping("/admin/business-hours")
    public BusinessHourResponse createBusinessHour(
            @Valid @RequestBody BusinessHourRequest request
    ) {
        return businessHourService.create(request);
    }

    @PutMapping("/admin/business-hours/{id}")
    public BusinessHourResponse updateBusinessHour(
            @PathVariable Long id,
            @Valid @RequestBody BusinessHourRequest request
    ) {
        return businessHourService.update(id, request);
    }

    @PutMapping("/admin/business-hours/day/{dayOfWeek}")
    public BusinessHourResponse updateBusinessHourByDay(
            @PathVariable DayOfWeek dayOfWeek,
            @Valid @RequestBody BusinessHourRequest request
    ) {
        return businessHourService.updateByDay(dayOfWeek, request);
    }
}