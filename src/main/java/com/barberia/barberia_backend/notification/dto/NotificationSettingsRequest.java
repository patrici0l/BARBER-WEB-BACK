package com.barberia.barberia_backend.notification.dto;

public record NotificationSettingsRequest(
        Integer reminderMinutesBefore,
        Boolean automaticEnabled,
        Boolean emailEnabled,
        Boolean whatsappEnabled,
        String whatsappBusinessNumber) {
}
