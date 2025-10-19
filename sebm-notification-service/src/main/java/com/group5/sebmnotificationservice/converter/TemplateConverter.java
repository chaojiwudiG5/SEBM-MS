package com.group5.sebmnotificationservice.converter;


import com.group5.sebmcommon.exception.ErrorCode;
import com.group5.sebmcommon.exception.ThrowUtils;
import com.group5.sebmmodels.dto.notification.CreateTemplateDto;
import com.group5.sebmmodels.vo.TemplateVo;
import com.group5.sebmmodels.entity.TemplatePo;
import com.group5.sebmmodels.dto.notification.TemplateDto;
import jakarta.servlet.http.HttpServletRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;

/**
 * 通知模板对象转换器
 */
@Mapper(componentModel = "spring")
public interface TemplateConverter {

    /**
     * CreateTemplateDto 转 TemplatePo
     * @param createTemplateDto 创建模板DTO
     * @param request HTTP请求对象
     * @return 模板PO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "request", qualifiedByName = "getUserIdFromRequest")
    @Mapping(target = "status", constant = "1")
    @Mapping(target = "isDelete", constant = "0")
    @Mapping(target = "createTime", expression = "java(getCurrentTime())")
    @Mapping(target = "updateTime", expression = "java(getCurrentTime())")
    @Mapping(target = "notificationNode", source = "createTemplateDto.notificationNode", qualifiedByName = "integerToString")
    @Mapping(target = "notificationRole", source = "createTemplateDto.notificationRole")
    @Mapping(target = "relateTimeOffset", source = "createTemplateDto.relateTimeOffset", qualifiedByName = "integerToLong")
    @Mapping(target = "templateContent", source = "createTemplateDto.content")
    @Mapping(target = "templateDesc", source = "createTemplateDto.templateDesc")
    TemplatePo toPo(CreateTemplateDto createTemplateDto, HttpServletRequest request);

    /**
     * TemplatePo 转 TemplateVo
     * @param templatePo 模板PO
     * @return 模板VO
     */
    @Mapping(target = "content", source = "templateContent")
    TemplateVo toVo(TemplatePo templatePo);

    /**
     * TemplatePo 转 TemplateDto
     * @param templatePo 模板PO
     * @return 模板DTO
     */
    @Mapping(target = "content", source = "templateContent")
    TemplateDto toDto(TemplatePo templatePo);

    /**
     * 从 HttpServletRequest 中获取当前用户ID
     * @param request HTTP请求对象
     * @return 用户ID
     */
    @Named("getUserIdFromRequest")
    default Long getUserIdFromRequest(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ThrowUtils.throwIf(userId == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        return userId;
    }

    
    /**
     * 获取当前时间
     * @return 当前时间
     */
    default LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

    /**
     * Integer转String
     * @param value Integer值
     * @return String值
     */
    @Named("integerToString")
    default String integerToString(Integer value) {
        return value != null ? value.toString() : null;
    }

    /**
     * Integer转Long
     * @param value Integer值
     * @return Long值
     */
    @Named("integerToLong")
    default Long integerToLong(Integer value) {
        return value != null ? value.longValue() : null;
    }
}