package com.barberia.barberia_backend.appointment;

import com.barberia.barberia_backend.common.enums.AppointmentStatus;
import com.barberia.barberia_backend.service.BarberService;
import com.barberia.barberia_backend.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Cliente dueño de la reserva.
     * Lo llamamos user para que coincida con AppointmentService y
     * AppointmentResponse.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private User user;

    /*
     * Servicio reservado.
     * Lo llamamos service para que coincida con AppointmentService y
     * AppointmentResponse.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private BarberService service;

    @Column(nullable = false)
    private LocalDate appointmentDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;
}
