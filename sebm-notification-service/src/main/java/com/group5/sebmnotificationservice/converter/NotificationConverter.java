package com.group5.sebmnotificationservice.converter;

import com.group5.sebmmodels.dto.notification.SendNotificationDto;
import com.group5.sebmmodels.entity.TemplatePo;
import com.group5.sebmnotificationservice.mq.NotificationMessage;
import com.group5.sebmmodels.dto.notification.TemplateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.UUID;

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
    @Mapping(target = "template", source = "templateDto", qualifiedByName = "templateDtoToPo")
    @Mapping(target = "templateVars", source = "sendNotificationDto.templateVars")
    @Mapping(target = "sendTime", ignore = true) // 发送时间由消息队列处理时设置
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
     * TemplateDto 转 TemplatePo
     * @param templateDto 模板DTO
     * @return 模板PO
     */
    @Named("templateDtoToPo")
    default TemplatePo templateDtoToPo(TemplateDto templateDto) {
        if (templateDto == null) {
            return null;
        }
        
        TemplatePo templatePo = new TemplatePo();
        templatePo.setId(templateDto.getId());
        templatePo.setTemplateTitle(templateDto.getTemplateTitle());
        templatePo.setNotificationMethod(templateDto.getNotificationMethod());
        templatePo.setNotificationNode(templateDto.getNotificationNode());
        templatePo.setNotificationRole(templateDto.getNotificationRole());
        templatePo.setNotificationType(templateDto.getNotificationType());
        templatePo.setNotificationEvent(templateDto.getNotificationEvent());
        templatePo.setRelateTimeOffset(templateDto.getRelateTimeOffset());
        templatePo.setUserId(templateDto.getUserId());
        templatePo.setTemplateContent(templateDto.getContent());
        templatePo.setTemplateDesc(templateDto.getTemplateDesc());
        templatePo.setStatus(templateDto.getStatus());
        templatePo.setIsDelete(templateDto.getIsDelete());
        templatePo.setCreateTime(templateDto.getCreateTime());
        templatePo.setUpdateTime(templateDto.getUpdateTime());
        
        return templatePo;
    }
}