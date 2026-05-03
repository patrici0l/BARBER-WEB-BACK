package com.barberia.barberia_backend.notification;

import com.barberia.barberia_backend.appointment.Appointment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentEmailService {

    private final EmailService emailService;

    public void sendAppointmentCreatedEmail(Appointment appointment) {
        String to = appointment.getUser().getEmail();
        String subject = "Reserva confirmada - Barbería";

        String body = """
                Hola %s,

                Tu reserva ha sido confirmada correctamente.

                Detalles de la reserva:

                Servicio: %s
                Fecha: %s
                Hora de inicio: %s
                Hora de fin: %s
                Estado: %s

                Gracias por reservar con nosotros.

                Barbería
                """.formatted(
                appointment.getUser().getName(),
                appointment.getService().getName(),
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus()
        );

        emailService.sendEmail(to, subject, body);
    }

    public void sendAppointmentCancelledEmail(Appointment appointment) {
        String to = appointment.getUser().getEmail();
        String subject = "Reserva cancelada - Barbería";

        String body = """
                Hola %s,

                Tu reserva ha sido cancelada correctamente.

                Detalles de la reserva cancelada:

                Servicio: %s
                Fecha: %s
                Hora de inicio: %s
                Hora de fin: %s
                Estado: %s

                El horario vuelve a estar disponible para nuevas reservas.

                Barbería
                """.formatted(
                appointment.getUser().getName(),
                appointment.getService().getName(),
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus()
        );

        emailService.sendEmail(to, subject, body);
    }
}