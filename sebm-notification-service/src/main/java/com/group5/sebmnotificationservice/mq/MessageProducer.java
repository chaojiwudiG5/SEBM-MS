package com.group5.sebmnotificationservice.mq;

import com.group5.sebmnotificationservice.service.NotificationRateLimiter;
import com.group5.sebmnotificationservice.service.NotificationTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.group5.sebmcommon.constant.NotificationConstant.NOTIFICATION_TOPIC;

/**
 * RabbitMQ 消息生产者服务 - 使用延迟消息插件实现高精度延迟
 */
@Slf4j
@Service
public class MessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private NotificationTaskService notificationTaskService;

    @Autowired
    private NotificationRateLimiter rateLimiter;

    /**
     * 发送即时通知消息
     * 即时消息不预先创建任务和记录，在消费时创建
     * @param message 通知消息
     * @return 是否发送成功
     */
    public boolean sendImmediateMessage(NotificationMessage message) {
        try {
            // 限流检查
            if (!rateLimiter.allowNotification(message.getUserId())) {
                log.warn("用户 {} 触发限流，拒绝发送即时通知", message.getUserId());
                return false;
            }

            // 设置消息ID和创建时间
            if (message.getMessageId() == null) {
                message.setMessageId(UUID.randomUUID().toString());
            }
            if (message.getCreateTime() == null) {
                message.setCreateTime(LocalDateTime.now());
            }

            // 即时消息不创建任务和记录，直接发送到MQ
            // 发送消息到RabbitMQ
            rabbitTemplate.convertAndSend(
                    NOTIFICATION_TOPIC,
                    "notification.immediate",
                    message
            );

            log.info("即时通知消息发送成功: messageId={}, userId={}",
                    message.getMessageId(), message.getUserId());
            return true;

        } catch (Exception e) {
            log.error("即时通知消息发送失败: messageId={}, error={}",
                    message.getMessageId(), e.getMessage(), e);
        }
        return false;
    }

    /**
     * 发送延迟通知消息 - 使用延迟消息插件实现高精度延迟
     * 延迟消息会先创建通知任务，在消费时创建记录
     * @param message 通知消息
     * @param delaySeconds 延迟时间（秒）
     * @return 是否发送成功
     */
    public boolean sendDelayMessage(NotificationMessage message, long delaySeconds) {
        try {
            // 限流检查
            if (!rateLimiter.allowNotification(message.getUserId())) {
                log.warn("用户 {} 触发限流，拒绝发送延迟通知", message.getUserId());
                return false;
            }

            // 设置消息ID和创建时间
            if (message.getMessageId() == null) {
                message.setMessageId(UUID.randomUUID().toString());
            }
            if (message.getCreateTime() == null) {
                message.setCreateTime(LocalDateTime.now());
            }

            // 先创建通知任务（不创建记录，记录在消费时创建）
            Long taskId = notificationTaskService.createTask(
                    message.getTemplate().getTemplateTitle(),
                    message.getTemplate().getTemplateContent(),
                    message.getTemplate().getNotificationRole()
            );
            
            if (taskId != null) {
                message.setTaskId(taskId);
                log.info("延迟通知任务已创建: taskId={}, messageId={}", taskId, message.getMessageId());
            } else {
                log.error("创建延迟通知任务失败: messageId={}", message.getMessageId());
                return false;
            }

            // 使用延迟消息插件发送消息
            // 延迟时间以毫秒为单位
            long delayMillis = delaySeconds * 1000;
            
            rabbitTemplate.convertAndSend(
                    "notification.delay.exchange",
                    "notification.delay",
                    message,
                    msg -> {
                        // 设置延迟时间（毫秒）- 使用延迟消息插件的header
                        msg.getMessageProperties().setHeader("x-delay", delayMillis);
                        return msg;
                    }
            );

            log.info("延迟通知消息发送成功: messageId={}, userId={}, taskId={}, delaySeconds={}, delayMillis={}",
                    message.getMessageId(), message.getUserId(), taskId, delaySeconds, delayMillis);
            return true;

        } catch (Exception e) {
            log.error("延迟通知消息发送失败: messageId={}, error={}",
                    message.getMessageId(), e.getMessage(), e);
        }
        return false;
    }
}