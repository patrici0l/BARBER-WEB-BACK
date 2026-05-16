package com.barberia.barberia_backend.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {

    List<NotificationHistory> findTop80ByOrderByCreatedAtDesc();

    boolean existsByAppointmentIdAndChannelAndTriggerTypeAndStatusIn(
            Long appointmentId,
            NotificationChannel channel,
            NotificationTriggerType triggerType,
            Collection<NotificationStatus> statuses);
}
