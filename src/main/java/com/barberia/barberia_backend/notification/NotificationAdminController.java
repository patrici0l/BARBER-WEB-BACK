package com.barberia.barberia_backend.notification;

import com.barberia.barberia_backend.notification.dto.ManualNotificationRequest;
import com.barberia.barberia_backend.notification.dto.NotificationHistoryResponse;
import com.barberia.barberia_backend.notification.dto.NotificationSettingsRequest;
import com.barberia.barberia_backend.notification.dto.NotificationSettingsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
public class NotificationAdminController {

    private final NotificationService notificationService;

    @GetMapping("/settings")
    public NotificationSettingsResponse getSettings() {
        return notificationService.getSettings();
    }

    @PutMapping("/settings")
    public NotificationSettingsResponse updateSettings(@RequestBody NotificationSettingsRequest request) {
        return notificationService.updateSettings(request);
    }

    @GetMapping("/history")
    public List<NotificationHistoryResponse> getHistory() {
        return notificationService.getHistory();
    }

    @PostMapping("/manual")
    public List<NotificationHistoryResponse> sendManual(@RequestBody ManualNotificationRequest request) {
        return notificationService.sendManual(request);
    }
}
