package com.barberia.barberia_backend.appointment.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentRequest {

        @NotNull(message = "El servicio es obligatorio")
        private Long serviceId;

        @NotNull(message = "La fecha de la reserva es obligatoria")
        private LocalDate appointmentDate;

        @NotNull(message = "La hora de inicio es obligatoria")
        private LocalTime startTime;

        public AppointmentRequest() {
        }

        public AppointmentRequest(Long serviceId, LocalDate appointmentDate, LocalTime startTime) {
                this.serviceId = serviceId;
                this.appointmentDate = appointmentDate;
                this.startTime = startTime;
        }

        public Long getServiceId() {
                return serviceId;
        }

        public LocalDate getAppointmentDate() {
                return appointmentDate;
        }

        public LocalTime getStartTime() {
                return startTime;
        }

        public void setServiceId(Long serviceId) {
                this.serviceId = serviceId;
        }

        public void setAppointmentDate(LocalDate appointmentDate) {
                this.appointmentDate = appointmentDate;
        }

        public void setStartTime(LocalTime startTime) {
                this.startTime = startTime;
        }
}