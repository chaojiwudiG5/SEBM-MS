package com.group5.sebmnotificationservice.service;

import com.group5.sebmnotificationservice.enums.NotificationMethodEnum;
import com.group5.sebmnotificationservice.sender.ChannelMsgSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息发送服务
 * 管理所有渠道消息发送器
 */
@Slf4j
@Service
public class MessageSenderService {
    
    private final Map<NotificationMethodEnum, ChannelMsgSender> senderMap = new ConcurrentHashMap<>();
    
    /**
     * 构造函数，自动注册所有发送器
     * @param senders 发送器列表
     */
    @Autowired
    public MessageSenderService(List<ChannelMsgSender> senders) {
        for (ChannelMsgSender sender : senders) {
            senderMap.put(sender.getChannelType(), sender);
            log.info("注册消息发送器: {}", sender.getChannelType().getDescription());
        }
    }
    
    /**
     * 根据渠道类型发送通知
     * @param notificationMethodEnum 渠道类型 (email, internal, sms 等)
     * @param userId 接收者
     * @param subject 主题
     * @param content 内容
     * @return 发送结果
     */
    public boolean sendNotification(NotificationMethodEnum notificationMethodEnum, Long userId, String subject, String content) {
        try {
            ChannelMsgSender sender = getSender(notificationMethodEnum);
            if (sender == null) {
                log.error("未找到对应的消息发送器: {}", notificationMethodEnum.getCode());
                return false;
            }
            return sender.sendNotification(userId, subject, content);
        } catch (Exception e) {
            log.error("发送通知失败 - 渠道: {}, 接收者: {}, 错误: {}", notificationMethodEnum.getCode(), userId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 获取指定类型的发送器
     * @param notificationMethod 渠道类型
     * @return ChannelMsgSender
     */
    public ChannelMsgSender getSender(NotificationMethodEnum notificationMethod) {
        return senderMap.get(notificationMethod);
    }}
