package com.barberia.barberia_backend.appointment;

import com.barberia.barberia_backend.appointment.dto.AppointmentRequest;
import com.barberia.barberia_backend.appointment.dto.AppointmentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/api/appointments")
    public AppointmentResponse createAppointment(
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication) {
        return appointmentService.createAppointment(request, authentication.getName());
    }

    @GetMapping("/api/my-appointments")
    public List<AppointmentResponse> getMyAppointments(Authentication authentication) {
        return appointmentService.getMyAppointments(authentication.getName());
    }

    @PutMapping("/api/appointments/{id}/cancel")
    public AppointmentResponse cancelAppointment(
            @PathVariable Long id,
            Authentication authentication) {
        return appointmentService.cancelAppointment(id, authentication.getName());
    }
}