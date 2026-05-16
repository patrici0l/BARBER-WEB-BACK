package com.barberia.barberia_backend.notification.dto;

import com.barberia.barberia_backend.notification.NotificationChannel;
import com.barberia.barberia_backend.notification.NotificationHistory;
import com.barberia.barberia_backend.notification.NotificationStatus;
import com.barberia.barberia_backend.notification.NotificationTriggerType;

import java.time.LocalDateTime;

public record NotificationHistoryResponse(
        Long id,
        Long appointmentId,
        String clientName,
        String serviceName,
        NotificationChannel channel,
        NotificationTriggerType triggerType,
        NotificationStatus status,
        String recipient,
        String subject,
        String message,
        LocalDateTime scheduledFor,
        LocalDateTime createdAt,
        String detail) {

    public static NotificationHistoryResponse fromEntity(NotificationHistory history) {
        var appointment = history.getAppointment();
        return new NotificationHistoryResponse(
                history.getId(),
                appointment != null ? appointment.getId() : null,
                appointment != null && appointment.getUser() != null ? appointment.getUser().getName() : null,
                appointment != null && appointment.getService() != null ? appointment.getService().getName() : null,
                history.getChannel(),
                history.getTriggerType(),
                history.getStatus(),
                history.getRecipient(),
                history.getSubject(),
                history.getMessage(),
                history.getScheduledFor(),
                history.getCreatedAt(),
                history.getDetail());
    }
}
