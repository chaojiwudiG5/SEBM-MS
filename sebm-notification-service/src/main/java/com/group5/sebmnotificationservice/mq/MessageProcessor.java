package com.group5.sebmnotificationservice.mq;

import cn.hutool.core.collection.CollectionUtil;
import com.group5.sebmmodels.entity.TemplatePo;
import com.group5.sebmnotificationservice.enums.NotificationMethodEnum;
import com.group5.sebmnotificationservice.enums.NotificationRecordStatusEnum;
import com.group5.sebmnotificationservice.service.MessageSenderService;
import com.group5.sebmnotificationservice.service.NotificationRecordService;
import com.group5.sebmnotificationservice.service.NotificationTaskService;
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
    private NotificationTaskService notificationTaskService;
    
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
     * 在消费时创建通知记录，并实际发送通知
     * @param message 消息
     * @return 是否处理成功
     */
    private void processByMethod(NotificationMessage message) {
        TemplatePo template = message.getTemplate();
        List<Integer> notificationMethods = template.getNotificationMethod();
        if(CollectionUtil.isEmpty(notificationMethods)) {
            return;
        }
        
        Long taskId = message.getTaskId();
        
        // 如果没有taskId，说明是即时消息，需要先创建任务
        if (taskId == null) {
            taskId = notificationTaskService.createTask(
                    template.getTemplateTitle(),
                    template.getTemplateContent(),
                    template.getNotificationRole()
            );
            if (taskId == null) {
                log.error("创建通知任务失败: messageId={}", message.getMessageId());
                return;
            }
            log.info("即时通知任务已创建: taskId={}, messageId={}", taskId, message.getMessageId());
        }
        
        // 遍历所有通知方式，为每种方式创建记录并发送
        for (Integer notificationMethod : notificationMethods) {
            NotificationMethodEnum method = NotificationMethodEnum.parseMethod(notificationMethod);
            if (method == null) {
                log.warn("无效的通知方式: method={}, messageId={}", notificationMethod, message.getMessageId());
                continue;
            }
            
            // 记录通知发送状态
            NotificationRecordStatusEnum recordStatus;
            
            try {
                // 实际发送通知
                messageSenderService.sendNotification(method, message.getUserId(), 
                        template.getTemplateTitle(), template.getTemplateContent());
                recordStatus = NotificationRecordStatusEnum.SUCCESS;
            } catch (Exception e) {
                recordStatus = NotificationRecordStatusEnum.FAILED;
            }
            
            // 创建通知记录
            boolean recordResult = notificationRecordService.createRecord(
                    taskId, 
                    message.getUserId(), 
                    notificationMethod, 
                    recordStatus.getCode()
            );
            
            if (recordResult) {
                log.info("通知记录已创建: taskId={}, userId={}, status={}",
                        taskId, message.getUserId(), recordStatus.getDesc());
            } else {
                log.error("创建通知记录失败: taskId={}, userId={}",
                        taskId, message.getUserId());
            }
        }
    }
}
