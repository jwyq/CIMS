package com.cims.backend.repository.notification;

/**
 * @autuor y5035
 * @since 2026-04-20
 * @description 通知消息数据访问仓储
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cims.backend.domain.notification.NotificationMessage;
import com.cims.backend.entity.notification.NotificationEntity;
import com.cims.backend.mapper.notification.NotificationMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class NotificationRepository {

    private static final String DEFAULT_NOTIFICATION_TITLE = "系统通知";
    private static final String DEFAULT_BIZ_TYPE = "LOAN";
    private static final String DEFAULT_RECIPIENT = "SYSTEM";

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
        entity.setTitle(DEFAULT_NOTIFICATION_TITLE);
        entity.setType(type);
        entity.setContent(content);
        entity.setBizType(DEFAULT_BIZ_TYPE);
        entity.setBizId(bizId);
        notificationMapper.insert(entity);
    }

    private NotificationMessage toDomain(NotificationEntity entity) {
        String recipient = entity.getType() == null ? DEFAULT_RECIPIENT : entity.getType();
        return new NotificationMessage(entity.getId(), recipient, entity.getContent());
    }
}
