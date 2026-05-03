package com.barberia.barberia_backend.appointment;

import com.barberia.barberia_backend.common.enums.AppointmentStatus;
import com.barberia.barberia_backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

        List<Appointment> findByAppointmentDateAndStatus(
                        LocalDate appointmentDate,
                        AppointmentStatus status);

        List<Appointment> findByUserOrderByAppointmentDateDescStartTimeDesc(User user);

        List<Appointment> findAllByOrderByAppointmentDateDescStartTimeDesc();

        boolean existsByAppointmentDateAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                        LocalDate appointmentDate,
                        AppointmentStatus status,
                        LocalTime endTime,
                        LocalTime startTime);
}