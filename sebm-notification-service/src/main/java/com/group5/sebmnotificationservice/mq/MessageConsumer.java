package com.group5.sebmnotificationservice.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ 消息消费者服务
 */
@Slf4j
@Service
public class MessageConsumer {

    @Autowired
    private MessageProcessor messageProcessor;

    /**
     * 消费即时通知消息
     */
    @RabbitListener(queues = "notification.immediate.queue")
    public void handleImmediateMessage(NotificationMessage message, Message amqpMessage) {
        try {
            log.info("接收到即时通知消息: messageId={}, userId={}", 
                    message.getMessageId(), message.getUserId());

            // 处理通知消息
            messageProcessor.processNotification(message);

            log.info("即时通知消息处理完成: messageId={}", message.getMessageId());

        } catch (Exception e) {
            log.error("即时通知消息处理异常: messageId={}, error={}", 
                    message.getMessageId(), e.getMessage(), e);
            // 这里可以添加重试逻辑
            throw e; // 重新抛出异常，让RabbitMQ进行重试
        }
    }

    /**
     * 消费延迟通知消息
     */
    @RabbitListener(queues = "notification.delay.queue")
    public void handleDelayMessage(NotificationMessage message, Message amqpMessage) {
        try {
            log.info("接收到延迟通知消息: messageId={}, userId={}", 
                    message.getMessageId(), message.getUserId());

            // 处理通知消息
            messageProcessor.processNotification(message);

            log.info("延迟通知消息处理完成: messageId={}", message.getMessageId());

        } catch (Exception e) {
            log.error("延迟通知消息处理异常: messageId={}, error={}", 
                    message.getMessageId(), e.getMessage(), e);
            // 这里可以添加重试逻辑
            throw e; // 重新抛出异常，让RabbitMQ进行重试
        }
    }

}