package com.barberia.barberia_backend.notification;

import com.barberia.barberia_backend.config.TimeConfig;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_settings")
@Getter
@Setter
@NoArgsConstructor
public class NotificationSettings {

    @Id
    private Long id = 1L;

    @Column(nullable = false)
    private Integer reminderMinutesBefore = 60;

    @Column(nullable = false)
    private Boolean automaticEnabled = true;

    @Column(nullable = false)
    private Boolean emailEnabled = true;

    @Column(nullable = false)
    private Boolean whatsappEnabled = false;

    private String whatsappBusinessNumber;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void touch() {
        updatedAt = LocalDateTime.now(TimeConfig.ZONE_ID);
    }
}
