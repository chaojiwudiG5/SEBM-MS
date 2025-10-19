package com.group5.sebmnotificationservice.sender;

import com.group5.sebmnotificationservice.enums.NotificationMethodEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * 渠道消息发送器抽象类
 * 定义消息发送的统一规范
 */
@Slf4j
public abstract class ChannelMsgSender {

    /**
     * 渠道类型
     */
    public abstract NotificationMethodEnum getChannelType();

    /**
     * 发送通知消息
     *
     * @param userId     接收者
     * @param subject    消息主题
     * @param content    消息内容
     * @return 是否发送成功
     */
    public abstract boolean sendNotification(Long userId, String subject, String content);

}
