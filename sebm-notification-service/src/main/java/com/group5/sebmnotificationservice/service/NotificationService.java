package com.group5.sebmnotificationservice.service;

import com.group5.sebmmodels.dto.notification.SendNotificationDto;


/**
 * 通知相关接口
 */
public interface NotificationService {
    /**
     * 发送通知
     * @param sendNotificationDto
     * @return
     */
     Boolean sendNotification(SendNotificationDto sendNotificationDto);
}
