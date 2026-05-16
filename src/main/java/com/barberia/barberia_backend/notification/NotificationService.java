package com.barberia.barberia_backend.notification;

import com.barberia.barberia_backend.appointment.Appointment;
import com.barberia.barberia_backend.appointment.AppointmentRepository;
import com.barberia.barberia_backend.common.enums.AppointmentStatus;
import com.barberia.barberia_backend.config.TimeConfig;
import com.barberia.barberia_backend.notification.dto.ManualNotificationRequest;
import com.barberia.barberia_backend.notification.dto.NotificationHistoryResponse;
import com.barberia.barberia_backend.notification.dto.NotificationSettingsRequest;
import com.barberia.barberia_backend.notification.dto.NotificationSettingsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Long SETTINGS_ID = 1L;

    private final NotificationSettingsRepository settingsRepository;
    private final NotificationHistoryRepository historyRepository;
    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;
    private final WhatsappService whatsappService;

    @Transactional
    public NotificationSettingsResponse getSettings() {
        return NotificationSettingsResponse.fromEntity(getOrCreateSettings());
    }

    @Transactional
    public NotificationSettingsResponse updateSettings(NotificationSettingsRequest request) {
        NotificationSettings settings = getOrCreateSettings();
        int minutes = request.reminderMinutesBefore() == null ? 60 : request.reminderMinutesBefore();
        if (minutes < 5 || minutes > 10080) {
            throw new IllegalArgumentException("El tiempo de notificacion debe estar entre 5 minutos y 7 dias.");
        }

        settings.setReminderMinutesBefore(minutes);
        settings.setAutomaticEnabled(Boolean.TRUE.equals(request.automaticEnabled()));
        settings.setEmailEnabled(Boolean.TRUE.equals(request.emailEnabled()));
        settings.setWhatsappEnabled(Boolean.TRUE.equals(request.whatsappEnabled()));
        settings.setWhatsappBusinessNumber(request.whatsappBusinessNumber());

        return NotificationSettingsResponse.fromEntity(settingsRepository.save(settings));
    }

    @Transactional(readOnly = true)
    public List<NotificationHistoryResponse> getHistory() {
        return historyRepository.findTop80ByOrderByCreatedAtDesc()
                .stream()
                .map(NotificationHistoryResponse::fromEntity)
                .toList();
    }

    @Transactional
    public List<NotificationHistoryResponse> sendManual(ManualNotificationRequest request) {
        Appointment appointment = appointmentRepository.findById(request.appointmentId())
                .orElseThrow(() -> new IllegalArgumentException("La reserva no existe."));

        String customMessage = request.message();
        return sendForAppointment(
                appointment,
                Boolean.TRUE.equals(request.email()),
                Boolean.TRUE.equals(request.whatsapp()),
                request.customEmail(),
                request.customWhatsapp(),
                NotificationTriggerType.MANUAL,
                customMessage,
                LocalDateTime.now(TimeConfig.ZONE_ID));
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void sendAutomaticReminders() {
        NotificationSettings settings = getOrCreateSettings();
        if (!Boolean.TRUE.equals(settings.getAutomaticEnabled())) {
            return;
        }

        LocalDateTime now = LocalDateTime.now(TimeConfig.ZONE_ID);
        LocalDateTime target = now.plusMinutes(settings.getReminderMinutesBefore());
        List<Appointment> appointments = appointmentRepository.findAppointmentsStartingBetween(
                AppointmentStatus.BOOKED,
                now.toLocalDate(),
                now.toLocalTime(),
                target.toLocalDate(),
                target.toLocalTime());

        for (Appointment appointment : appointments) {
            sendAutomaticIfNeeded(appointment, settings, target);
        }
    }

    private void sendAutomaticIfNeeded(Appointment appointment, NotificationSettings settings, LocalDateTime target) {
        boolean email = Boolean.TRUE.equals(settings.getEmailEnabled())
                && !alreadyAutomatic(appointment.getId(), NotificationChannel.EMAIL);
        boolean whatsapp = Boolean.TRUE.equals(settings.getWhatsappEnabled())
                && !alreadyAutomatic(appointment.getId(), NotificationChannel.WHATSAPP);

        if (!email && !whatsapp) {
            return;
        }

        sendForAppointment(
                appointment,
                email,
                whatsapp,
                null,
                null,
                NotificationTriggerType.AUTOMATIC,
                null,
                target);
    }

    private boolean alreadyAutomatic(Long appointmentId, NotificationChannel channel) {
        return historyRepository.existsByAppointmentIdAndChannelAndTriggerTypeAndStatusIn(
                appointmentId,
                channel,
                NotificationTriggerType.AUTOMATIC,
                List.of(NotificationStatus.SENT, NotificationStatus.SKIPPED));
    }

    private List<NotificationHistoryResponse> sendForAppointment(
            Appointment appointment,
            boolean email,
            boolean whatsapp,
            String customEmail,
            String customWhatsapp,
            NotificationTriggerType triggerType,
            String customMessage,
            LocalDateTime scheduledFor) {

        String subject = "Recordatorio de reserva - Barberia";
        String message = customMessage == null || customMessage.isBlank()
                ? buildReminderMessage(appointment)
                : customMessage;

        List<NotificationHistory> results = new java.util.ArrayList<>();

        if (email) {
            String recipient = customEmail == null || customEmail.isBlank()
                    ? appointment.getUser().getEmail()
                    : customEmail;
            DeliveryResult result = emailService.sendEmailWithResult(recipient, subject, message);
            results.add(saveHistory(appointment, NotificationChannel.EMAIL, triggerType, result, recipient, subject, message, scheduledFor));
        }

        if (whatsapp) {
            String recipient = customWhatsapp == null || customWhatsapp.isBlank()
                    ? appointment.getUser().getPhoneNumber()
                    : customWhatsapp;
            DeliveryResult result = whatsappService.sendWhatsapp(recipient, message);
            results.add(saveHistory(appointment, NotificationChannel.WHATSAPP, triggerType, result, recipient, subject, message, scheduledFor));
        }

        return results.stream().map(NotificationHistoryResponse::fromEntity).toList();
    }

    private NotificationHistory saveHistory(
            Appointment appointment,
            NotificationChannel channel,
            NotificationTriggerType triggerType,
            DeliveryResult result,
            String recipient,
            String subject,
            String message,
            LocalDateTime scheduledFor) {
        return historyRepository.save(NotificationHistory.builder()
                .appointment(appointment)
                .channel(channel)
                .triggerType(triggerType)
                .status(result.status())
                .recipient(recipient)
                .subject(subject)
                .message(message)
                .scheduledFor(scheduledFor)
                .createdAt(LocalDateTime.now(TimeConfig.ZONE_ID))
                .detail(result.detail())
                .build());
    }

    private String buildReminderMessage(Appointment appointment) {
        return """
                Hola %s,

                Te recordamos tu cita en Barberia.

                Servicio: %s
                Fecha: %s
                Hora: %s

                Te esperamos.
                """.formatted(
                appointment.getUser().getName(),
                appointment.getService().getName(),
                appointment.getAppointmentDate(),
                appointment.getStartTime());
    }

    private NotificationSettings getOrCreateSettings() {
        return settingsRepository.findById(SETTINGS_ID)
                .orElseGet(() -> settingsRepository.save(new NotificationSettings()));
    }
}
