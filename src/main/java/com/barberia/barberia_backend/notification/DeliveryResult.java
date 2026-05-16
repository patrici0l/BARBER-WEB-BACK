package com.barberia.barberia_backend.notification;

public record DeliveryResult(NotificationStatus status, String detail) {
    public static DeliveryResult sent(String detail) {
        return new DeliveryResult(NotificationStatus.SENT, detail);
    }

    public static DeliveryResult skipped(String detail) {
        return new DeliveryResult(NotificationStatus.SKIPPED, detail);
    }

    public static DeliveryResult failed(String detail) {
        return new DeliveryResult(NotificationStatus.FAILED, detail);
    }
}
