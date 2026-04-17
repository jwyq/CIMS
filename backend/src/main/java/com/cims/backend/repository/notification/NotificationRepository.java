package com.cims.backend.repository.notification;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cims.backend.domain.notification.NotificationMessage;
import com.cims.backend.entity.notification.NotificationEntity;
import com.cims.backend.mapper.notification.NotificationMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class NotificationRepository {

    private final NotificationMapper notificationMapper;

    public NotificationRepository(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    public List<NotificationMessage> findAllNotifications() {
        return notificationMapper.selectList(new LambdaQueryWrapper<NotificationEntity>().orderByDesc(NotificationEntity::getId))
            .stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    public void createNotification(String type, String content, Long bizId) {
        NotificationEntity entity = new NotificationEntity();
        entity.setTitle("系统通知");
        entity.setType(type);
        entity.setContent(content);
        entity.setBizType("LOAN");
        entity.setBizId(bizId);
        notificationMapper.insert(entity);
    }

    private NotificationMessage toDomain(NotificationEntity entity) {
        String recipient = entity.getType() == null ? "SYSTEM" : entity.getType();
        return new NotificationMessage(entity.getId(), recipient, entity.getContent());
    }
}
