package com.cims.backend.service.notification;

import com.cims.backend.domain.notification.NotificationMessage;
import com.cims.backend.repository.notification.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<NotificationMessage> listNotifications() {
        return notificationRepository.findAllNotifications();
    }
}
