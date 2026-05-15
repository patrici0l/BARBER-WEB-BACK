package com.barberia.barberia_backend.appointment;

import com.barberia.barberia_backend.appointment.dto.AppointmentRequest;
import com.barberia.barberia_backend.appointment.dto.AppointmentResponse;
import com.barberia.barberia_backend.businesshour.BusinessHour;
import com.barberia.barberia_backend.businesshour.BusinessHourRepository;
import com.barberia.barberia_backend.common.enums.AppointmentStatus;
import com.barberia.barberia_backend.common.enums.Role;
import com.barberia.barberia_backend.notification.AppointmentEmailService;
import com.barberia.barberia_backend.service.BarberService;
import com.barberia.barberia_backend.service.BarberServiceRepository;
import com.barberia.barberia_backend.user.User;
import com.barberia.barberia_backend.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private static final int SLOT_INTERVAL_MINUTES = 30;

    private final AppointmentRepository appointmentRepository;
    private final BarberServiceRepository barberServiceRepository;
    private final BusinessHourRepository businessHourRepository;
    private final UserRepository userRepository;
    private final AppointmentEmailService appointmentEmailService;
    private final Clock clock;

    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        BarberService service = barberServiceRepository.findById(request.getServiceId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Servicio no encontrado con id: " + request.getServiceId()));

        if (!Boolean.TRUE.equals(service.getActive())) {
            throw new IllegalArgumentException("El servicio seleccionado no está activo");
        }

        if (service.getDurationMinutes() == null || service.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("El servicio seleccionado no tiene una duración válida");
        }

        LocalDate appointmentDate = request.getAppointmentDate();
        LocalTime startTime = normalizeTime(request.getStartTime());
        LocalTime endTime = startTime.plusMinutes(service.getDurationMinutes());

        validateAppointmentIsNotInPast(appointmentDate, startTime);
        validateSlotAlignment(startTime);
        validateBusinessHour(appointmentDate, startTime, endTime);
        validateNoOverlapForCreate(appointmentDate, startTime, endTime);

        Appointment appointment = Appointment.builder()
                .user(user)
                .service(service)
                .appointmentDate(appointmentDate)
                .startTime(startTime)
                .endTime(endTime)
                .status(AppointmentStatus.BOOKED)
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

        try {
            appointmentEmailService.sendAppointmentCreatedEmail(savedAppointment);
        } catch (Exception ignored) {
            // El correo no debe revertir la reserva.
        }

        return AppointmentResponse.fromEntity(savedAppointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getMyAppointments(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        return appointmentRepository.findByUserOrderByAppointmentDateDescStartTimeDesc(user)
                .stream()
                .map(AppointmentResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAllByOrderByAppointmentDateDescStartTimeDesc()
                .stream()
                .map(AppointmentResponse::fromEntity)
                .toList();
    }

    @Transactional
    public AppointmentResponse cancelAppointment(Long id, String userEmail) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con id: " + id));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        boolean isOwner = appointment.getUser() != null && appointment.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("No tienes permisos para cancelar esta reserva");
        }

        validateAppointmentCanBeCancelled(appointment);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment cancelledAppointment = appointmentRepository.save(appointment);

        try {
            appointmentEmailService.sendAppointmentCancelledEmail(cancelledAppointment);
        } catch (Exception ignored) {
            // El correo no debe revertir la cancelación.
        }

        return AppointmentResponse.fromEntity(cancelledAppointment);
    }

    @Transactional
    public AppointmentResponse completeAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con id: " + id));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalArgumentException("No puedes completar una reserva cancelada");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        return AppointmentResponse.fromEntity(appointmentRepository.save(appointment));
    }

    private LocalTime normalizeTime(LocalTime time) {
        if (time == null) {
            throw new IllegalArgumentException("La hora de inicio es obligatoria");
        }
        return time.withSecond(0).withNano(0);
    }

    private void validateAppointmentIsNotInPast(LocalDate appointmentDate, LocalTime startTime) {
        if (appointmentDate == null) {
            throw new IllegalArgumentException("La fecha de la reserva es obligatoria");
        }

        LocalDate today = LocalDate.now(clock);
        LocalTime now = LocalTime.now(clock);

        if (appointmentDate.isBefore(today)) {
            throw new IllegalArgumentException("No puedes reservar en una fecha pasada");
        }

        if (appointmentDate.isEqual(today) && !startTime.isAfter(now)) {
            throw new IllegalArgumentException("No puedes reservar en una hora pasada");
        }
    }

    private void validateSlotAlignment(LocalTime startTime) {
        int totalMinutes = startTime.getHour() * 60 + startTime.getMinute();

        if (totalMinutes % SLOT_INTERVAL_MINUTES != 0) {
            throw new IllegalArgumentException(
                    "La hora debe iniciar en intervalos de " + SLOT_INTERVAL_MINUTES + " minutos");
        }
    }

    private void validateBusinessHour(LocalDate appointmentDate, LocalTime startTime, LocalTime endTime) {
        DayOfWeek dayOfWeek = appointmentDate.getDayOfWeek();

        BusinessHour businessHour = businessHourRepository.findByDayOfWeek(dayOfWeek)
                .orElseThrow(() -> new EntityNotFoundException("No hay horario configurado para el día: " + dayOfWeek));

        if (!Boolean.TRUE.equals(businessHour.getActive())
                || businessHour.getOpenTime() == null
                || businessHour.getCloseTime() == null) {
            throw new IllegalArgumentException("La barbería está cerrada este día");
        }

        if (startTime.isBefore(businessHour.getOpenTime()) || endTime.isAfter(businessHour.getCloseTime())) {
            throw new IllegalArgumentException("La reserva está fuera del horario de atención ("
                    + businessHour.getOpenTime() + " - " + businessHour.getCloseTime() + ")");
        }
    }

    private void validateNoOverlapForCreate(LocalDate appointmentDate, LocalTime startTime, LocalTime endTime) {
        List<Appointment> overlaps = appointmentRepository.findOverlappingAppointmentsForUpdate(
                appointmentDate,
                AppointmentStatus.BOOKED,
                startTime,
                endTime);

        if (!overlaps.isEmpty()) {
            throw new IllegalArgumentException("El horario seleccionado ya está ocupado por otra cita");
        }
    }

    private void validateAppointmentCanBeCancelled(Appointment appointment) {
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalArgumentException("La reserva ya está cancelada");
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalArgumentException("No puedes cancelar una reserva completada");
        }

        LocalDate today = LocalDate.now(clock);
        LocalTime now = LocalTime.now(clock);

        if (appointment.getAppointmentDate().isBefore(today)
                || (appointment.getAppointmentDate().isEqual(today) && !appointment.getStartTime().isAfter(now))) {
            throw new IllegalArgumentException("No puedes cancelar una reserva que ya pasó o está en curso");
        }
    }
}