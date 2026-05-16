package com.barberia.barberia_backend.notification.dto;

import com.barberia.barberia_backend.notification.NotificationSettings;

import java.time.LocalDateTime;

public record NotificationSettingsResponse(
        Integer reminderMinutesBefore,
        Boolean automaticEnabled,
        Boolean emailEnabled,
        Boolean whatsappEnabled,
        String whatsappBusinessNumber,
        LocalDateTime updatedAt) {

    public static NotificationSettingsResponse fromEntity(NotificationSettings settings) {
        return new NotificationSettingsResponse(
                settings.getReminderMinutesBefore(),
                settings.getAutomaticEnabled(),
                settings.getEmailEnabled(),
                settings.getWhatsappEnabled(),
                settings.getWhatsappBusinessNumber(),
                settings.getUpdatedAt());
    }
}
