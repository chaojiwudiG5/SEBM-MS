package com.group5.sebmnotificationservice.mq;

import cn.hutool.core.collection.CollectionUtil;
import com.group5.sebmmodels.entity.TemplatePo;
import com.group5.sebmnotificationservice.enums.NotificationMethodEnum;
import com.group5.sebmnotificationservice.enums.NotificationRecordStatusEnum;
import com.group5.sebmnotificationservice.service.MessageSenderService;
import com.group5.sebmnotificationservice.service.NotificationRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通知消息处理器
 */
@Slf4j
@Service
public class MessageProcessor {

    @Autowired
    private MessageSenderService messageSenderService;
    
    @Autowired
    private NotificationRecordService notificationRecordService;

    
    /**
     * 处理通知消息
     * @param message 通知消息
     * @return 是否处理成功
     */
    public void processNotification(NotificationMessage message) {
        try {
            log.info("开始处理通知消息: messageId={}, recipient={}", message.getMessageId(), message.getUserId());
            
            // 1. 验证消息
            if (!validateMessage(message)) {
                log.error("通知消息验证失败: messageId={}", message.getMessageId());
                return;
            }
            
            // 2. 根据通知方式处理消息
            processByMethod(message);
        } catch (Exception e) {
            log.error("处理通知消息时发生异常: messageId={}, error={}", 
                    message.getMessageId(), e.getMessage(), e);
        }
    }
    
    /**
     * 验证消息
     * @param message 消息
     * @return 是否有效
     */
    private boolean validateMessage(NotificationMessage message) {
        if (message == null || message.getUserId() == null) {
            log.error("接收者为空: messageId={}", message.getMessageId());
            return false;
        }

        TemplatePo template = message.getTemplate();
        
        if (template == null) {
            return false;
        }
        
        if (CollectionUtil.isEmpty(template.getNotificationMethod())) {
            log.error("通知方式为空: messageId={}", message.getMessageId());
            return false;
        }
        
        if (!NotificationMethodEnum.isValidCode(template.getNotificationMethod())) {
            log.error("无效的通知方式: messageId={}, method={}", message.getMessageId(), template.getNotificationMethod());
            return false;
        }
        
        return true;
    }
    
    /**
     * 根据通知方式处理消息
     * @param message 消息
     * @return 是否处理成功
     */
    private void processByMethod(NotificationMessage message) {
        TemplatePo template = message.getTemplate();
        List<Integer> notificationMethods = template.getNotificationMethod();
        if(CollectionUtil.isEmpty(notificationMethods)) {
            return;
        }
        
        // 记录通知发送状态（默认为成功）
        NotificationRecordStatusEnum recordStatus = NotificationRecordStatusEnum.SUCCESS;
        
        try {
            for (Integer notificationMethod : notificationMethods) {
                NotificationMethodEnum method = NotificationMethodEnum.parseMethod(notificationMethod);
                if (method == null) {
                    continue;
                }
                messageSenderService.sendNotification(method, message.getUserId(), template.getTemplateTitle(), template.getTemplateContent());
            }
        } catch (Exception e) {
            log.error("发送通知失败: messageId={}, error={}", message.getMessageId(), e.getMessage(), e);
            recordStatus = NotificationRecordStatusEnum.FAILED;
        }
        
        // 保存或更新通知记录
        if (message.getRecordId() != null) {
            // 延时通知：更新已有记录的状态
            boolean updated = notificationRecordService.updateRecordStatus(
                    message.getRecordId(),
                    recordStatus.getCode()
            );
            if (updated) {
                log.info("延迟通知状态已更新: recordId={}, status={}", 
                        message.getRecordId(), recordStatus.getDesc());
            }
        } else {
            // 即时通知：创建新记录
            notificationRecordService.saveNotificationRecord(
                    message.getUserId(),
                    template.getTemplateTitle(),
                    template.getTemplateContent(),
                    recordStatus.getCode()
            );
        }
    }
}
