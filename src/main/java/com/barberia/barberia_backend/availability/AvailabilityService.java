package com.barberia.barberia_backend.availability;

import com.barberia.barberia_backend.appointment.Appointment;
import com.barberia.barberia_backend.appointment.AppointmentRepository;
import com.barberia.barberia_backend.businesshour.BusinessHour;
import com.barberia.barberia_backend.businesshour.BusinessHourRepository;
import com.barberia.barberia_backend.common.enums.AppointmentStatus;
import com.barberia.barberia_backend.service.BarberService;
import com.barberia.barberia_backend.service.BarberServiceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

        private static final int SLOT_INTERVAL_MINUTES = 30;
        private static final int MIN_ADVANCE_MINUTES = 15;

        private final BusinessHourRepository businessHourRepository;
        private final BarberServiceRepository barberServiceRepository;
        private final AppointmentRepository appointmentRepository;
        private final Clock clock;

        @Transactional(readOnly = true)
        public List<AvailabilitySlotResponse> getAvailability(LocalDate date, Long serviceId) {
                if (date == null) {
                        throw new IllegalArgumentException("La fecha es obligatoria");
                }

                if (serviceId == null) {
                        throw new IllegalArgumentException("El servicio es obligatorio");
                }

                BarberService service = barberServiceRepository.findById(serviceId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Servicio no encontrado con id: " + serviceId));

                if (!Boolean.TRUE.equals(service.getActive())) {
                        return List.of();
                }

                Integer durationMinutes = service.getDurationMinutes();

                if (durationMinutes == null || durationMinutes <= 0) {
                        throw new IllegalArgumentException("El servicio no tiene una duración válida");
                }

                DayOfWeek dayOfWeek = date.getDayOfWeek();

                BusinessHour businessHour = businessHourRepository.findByDayOfWeek(dayOfWeek)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "No hay horario configurado para el día: " + dayOfWeek));

                if (!Boolean.TRUE.equals(businessHour.getActive())
                                || businessHour.getOpenTime() == null
                                || businessHour.getCloseTime() == null) {
                        return List.of();
                }

                List<Appointment> bookedAppointments = appointmentRepository.findByAppointmentDateAndStatus(
                                date,
                                AppointmentStatus.BOOKED);

                List<AvailabilitySlotResponse> slots = new ArrayList<>();
                LocalTime currentStartTime = businessHour.getOpenTime();

                while (!currentStartTime.plusMinutes(durationMinutes).isAfter(businessHour.getCloseTime())) {
                        LocalTime currentEndTime = currentStartTime.plusMinutes(durationMinutes);

                        boolean available = !hasOverlap(currentStartTime, currentEndTime, bookedAppointments)
                                        && !isPastSlot(date, currentStartTime);

                        slots.add(new AvailabilitySlotResponse(currentStartTime, currentEndTime, available));

                        currentStartTime = currentStartTime.plusMinutes(SLOT_INTERVAL_MINUTES);
                }

                return slots;
        }

        private boolean hasOverlap(LocalTime slotStart, LocalTime slotEnd, List<Appointment> bookedAppointments) {
                return bookedAppointments.stream()
                                .anyMatch(appointment -> slotStart.isBefore(appointment.getEndTime())
                                                && slotEnd.isAfter(appointment.getStartTime()));
        }

        private boolean isPastSlot(LocalDate date, LocalTime slotStart) {
                LocalDate today = LocalDate.now(clock);

                if (date.isBefore(today)) {
                        return true;
                }

                if (date.isEqual(today)) {
                        return slotStart.isBefore(LocalTime.now(clock).plusMinutes(MIN_ADVANCE_MINUTES));
                }

                return false;
        }
}