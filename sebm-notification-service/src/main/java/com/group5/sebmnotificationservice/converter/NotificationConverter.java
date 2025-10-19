package com.group5.sebmnotificationservice.converter;

import com.group5.sebmmodels.dto.notification.SendNotificationDto;
import com.group5.sebmmodels.entity.TemplatePo;
import com.group5.sebmnotificationservice.mq.NotificationMessage;
import com.group5.sebmmodels.dto.notification.TemplateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通知消息转换器
 * 负责 DTO 和 PO 之间的转换，以及业务对象的构建
 * 按照分层架构：service层负责vo dto po和bo的转化
 */
@Mapper(componentModel = "spring")
public interface NotificationConverter {

    /**
     * 构建通知消息业务对象
     * SendNotificationDto + TemplateDto -> NotificationMessage(bo)
     * 
     * @param sendNotificationDto 发送通知请求DTO
     * @param templateDto 通知模板DTO
     * @return 通知消息业务对象
     */
    @Mapping(target = "messageId", expression = "java(generateMessageId())")
    @Mapping(target = "userId", source = "sendNotificationDto.userId")
    @Mapping(target = "template", expression = "java(buildTemplateWithPlaceholders(templateDto, sendNotificationDto.getTemplateVars()))")
    @Mapping(target = "templateVars", ignore = true)
    @Mapping(target = "sendTime", ignore = true) // 发送时间由消息队列处理时设置
    @Mapping(target = "taskId", ignore = true) // 任务ID在延时通知时由后续处理设置
    @Mapping(target = "retryCount", constant = "0")
    @Mapping(target = "maxRetryCount", constant = "3")
    @Mapping(target = "createTime", expression = "java(getCurrentTime())")
    NotificationMessage buildNotificationMessage(SendNotificationDto sendNotificationDto, TemplateDto templateDto);

    /**
     * 生成消息ID
     * @return UUID字符串
     */
    @Named("generateMessageId")
    default String generateMessageId() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取当前时间
     * @return 当前时间
     */
    @Named("getCurrentTime")
    default LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

    /**
     * TemplateDto 转 TemplatePo，并替换占位符
     * @param templateDto 模板DTO
     * @param templateVars 占位符变量
     * @return 模板PO（已替换占位符）
     */
    @Named("buildTemplateWithPlaceholders")
    default TemplatePo buildTemplateWithPlaceholders(TemplateDto templateDto, Map<String, Object> templateVars) {
        if (templateDto == null) {
            return null;
        }
        
        TemplatePo templatePo = new TemplatePo();
        templatePo.setId(templateDto.getId());
        
        // 替换标题中的占位符
        String title = templateDto.getTemplateTitle();
        if (title != null && templateVars != null && !templateVars.isEmpty()) {
            title = replacePlaceholders(title, templateVars);
        }
        templatePo.setTemplateTitle(title);
        
        // 替换内容中的占位符
        String content = templateDto.getContent();
        if (content != null && templateVars != null && !templateVars.isEmpty()) {
            content = replacePlaceholders(content, templateVars);
        }
        templatePo.setTemplateContent(content);
        
        templatePo.setNotificationMethod(templateDto.getNotificationMethod());
        templatePo.setNotificationNode(templateDto.getNotificationNode());
        templatePo.setNotificationRole(templateDto.getNotificationRole());
        templatePo.setNotificationType(templateDto.getNotificationType());
        templatePo.setNotificationEvent(templateDto.getNotificationEvent());
        templatePo.setRelateTimeOffset(templateDto.getRelateTimeOffset());
        templatePo.setUserId(templateDto.getUserId());
        templatePo.setTemplateDesc(templateDto.getTemplateDesc());
        templatePo.setStatus(templateDto.getStatus());
        templatePo.setIsDelete(templateDto.getIsDelete());
        templatePo.setCreateTime(templateDto.getCreateTime());
        templatePo.setUpdateTime(templateDto.getUpdateTime());
        
        return templatePo;
    }
    
    /**
     * 替换字符串中的占位符
     * 支持 {key} 格式的占位符
     * 
     * @param text 原始文本
     * @param variables 变量映射
     * @return 替换后的文本
     */
    default String replacePlaceholders(String text, Map<String, Object> variables) {
        if (text == null || variables == null || variables.isEmpty()) {
            return text;
        }
        
        // 匹配 {key} 格式的占位符
        Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(text);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = variables.get(key);
            // 如果找到对应的变量值，则替换；否则保持原样
            String replacement = value != null ? value.toString() : matcher.group(0);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        
        return result.toString();
    }
}