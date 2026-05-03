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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

        private final BusinessHourRepository businessHourRepository;
        private final BarberServiceRepository barberServiceRepository;
        private final AppointmentRepository appointmentRepository;

        @Transactional(readOnly = true)
        public List<AvailabilitySlotResponse> getAvailability(LocalDate date, Long serviceId) {
                BarberService service = barberServiceRepository.findById(serviceId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Servicio no encontrado con id: " + serviceId));

                if (!Boolean.TRUE.equals(service.getActive())) {
                        return List.of();
                }

                DayOfWeek dayOfWeek = date.getDayOfWeek();

                BusinessHour businessHour = businessHourRepository.findByDayOfWeek(dayOfWeek)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "No hay horario configurado para el día: " + dayOfWeek));

                if (!Boolean.TRUE.equals(businessHour.getActive())) {
                        return List.of();
                }

                if (businessHour.getOpenTime() == null || businessHour.getCloseTime() == null) {
                        return List.of();
                }

                LocalTime openTime = businessHour.getOpenTime();
                LocalTime closeTime = businessHour.getCloseTime();

                int durationMinutes = service.getDurationMinutes();

                if (durationMinutes <= 0) {
                        throw new IllegalArgumentException("La duración del servicio debe ser mayor a 0");
                }

                List<Appointment> bookedAppointments = appointmentRepository.findByAppointmentDateAndStatus(
                                date,
                                AppointmentStatus.BOOKED);

                List<AvailabilitySlotResponse> slots = new ArrayList<>();

                LocalTime currentStartTime = openTime;

                while (!currentStartTime.plusMinutes(durationMinutes).isAfter(closeTime)) {
                        LocalTime currentEndTime = currentStartTime.plusMinutes(durationMinutes);

                        boolean overlaps = hasOverlap(
                                        currentStartTime,
                                        currentEndTime,
                                        bookedAppointments);

                        boolean isPastSlot = isPastSlot(date, currentStartTime);

                        boolean available = !overlaps && !isPastSlot;

                        slots.add(new AvailabilitySlotResponse(
                                        currentStartTime,
                                        currentEndTime,
                                        available));

                        currentStartTime = currentEndTime;
                }

                return slots;
        }

        private boolean hasOverlap(
                        LocalTime slotStart,
                        LocalTime slotEnd,
                        List<Appointment> bookedAppointments) {
                return bookedAppointments.stream()
                                .anyMatch(appointment -> slotStart.isBefore(appointment.getEndTime())
                                                && slotEnd.isAfter(appointment.getStartTime()));
        }

        private boolean isPastSlot(LocalDate date, LocalTime slotStart) {
                LocalDate today = LocalDate.now();
                LocalTime now = LocalTime.now();

                return date.isBefore(today)
                                || date.isEqual(today) && slotStart.isBefore(now);
        }
}