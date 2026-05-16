package com.barberia.barberia_backend.appointment;

import com.barberia.barberia_backend.appointment.dto.AppointmentRequest;
import com.barberia.barberia_backend.appointment.dto.AppointmentResponse;
import com.barberia.barberia_backend.auth.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final JwtService jwtService;

    @PostMapping("/api/appointments")
    public AppointmentResponse createAppointment(
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication) {
        return createAuthenticatedAppointment(request, authentication);
    }

    @PostMapping("/api/my-appointments")
    public AppointmentResponse createMyAppointment(
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication) {
        return createAuthenticatedAppointment(request, authentication);
    }

    @PostMapping("/api/reservations")
    public AppointmentResponse createReservation(
            @Valid @RequestBody AppointmentRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        return createAuthenticatedAppointment(request, getEmailFromAuthorizationHeader(authorizationHeader));
    }

    private AppointmentResponse createAuthenticatedAppointment(
            AppointmentRequest request,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("Debes iniciar sesión para reservar");
        }

        return appointmentService.createAppointment(request, authentication.getName());
    }

    private AppointmentResponse createAuthenticatedAppointment(
            AppointmentRequest request,
            String userEmail) {
        if (userEmail == null || userEmail.isBlank()) {
            throw new AuthenticationCredentialsNotFoundException("Debes iniciar sesión para reservar");
        }

        return appointmentService.createAppointment(request, userEmail);
    }

    private String getEmailFromAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }

        return jwtService.extractUsername(authorizationHeader.substring(7));
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
