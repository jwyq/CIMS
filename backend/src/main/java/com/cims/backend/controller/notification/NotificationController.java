package com.cims.backend.controller.notification;

import com.cims.backend.domain.notification.NotificationMessage;
import com.cims.backend.dto.ApiResponse;
import com.cims.backend.service.notification.NotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ApiResponse<List<NotificationMessage>> notifications() {
        return ApiResponse.success(notificationService.listNotifications());
    }
}
