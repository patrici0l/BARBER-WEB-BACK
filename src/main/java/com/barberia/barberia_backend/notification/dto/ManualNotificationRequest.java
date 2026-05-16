package com.barberia.barberia_backend.notification.dto;

public record ManualNotificationRequest(
        Long appointmentId,
        Boolean email,
        Boolean whatsapp,
        String customEmail,
        String customWhatsapp,
        String message) {
}
