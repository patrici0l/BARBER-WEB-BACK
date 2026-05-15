package com.barberia.barberia_backend.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BarberServiceController {

    private final BarberServiceService barberServiceService;

    @GetMapping("/services")
    public List<BarberServiceResponse> getActiveServices() {
        return barberServiceService.findAllActive();
    }

    @GetMapping("/admin/services")
    public List<BarberServiceResponse> getAllServicesForAdmin() {
        return barberServiceService.findAll();
    }

    @GetMapping("/admin/services/{id}")
    public BarberServiceResponse getServiceById(@PathVariable Long id) {
        return barberServiceService.findById(id);
    }

    @PostMapping("/admin/services")
    public BarberServiceResponse createService(
            @Valid @RequestBody BarberServiceRequest request) {
        return barberServiceService.create(request);
    }

    @PutMapping("/admin/services/{id}")
    public BarberServiceResponse updateService(
            @PathVariable Long id,
            @Valid @RequestBody BarberServiceRequest request) {
        return barberServiceService.update(id, request);
    }

    @PatchMapping("/admin/services/{id}/deactivate")
    public BarberServiceResponse deactivateService(@PathVariable Long id) {
        return barberServiceService.deactivate(id);
    }

    @PatchMapping("/admin/services/{id}/activate")
    public BarberServiceResponse activateService(@PathVariable Long id) {
        return barberServiceService.activate(id);
    }
}