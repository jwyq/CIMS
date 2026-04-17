package com.cims.backend.mapper.notification;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cims.backend.entity.notification.NotificationEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotificationMapper extends BaseMapper<NotificationEntity> {
}
