package com.barberia.barberia_backend.appointment;

import com.barberia.barberia_backend.appointment.dto.AppointmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/appointments")
@RequiredArgsConstructor
public class AdminAppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @PutMapping("/{id}/complete")
    public AppointmentResponse completeAppointment(@PathVariable Long id) {
        return appointmentService.completeAppointment(id);
    }
}