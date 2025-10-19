package com.group5.sebmnotificationservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.group5.sebmmodels.dto.notification.CreateTemplateDto;
import com.group5.sebmmodels.dto.notification.TemplateQueryDto;
import com.group5.sebmmodels.dto.notification.UpdateTemplateDto;
import com.group5.sebmmodels.vo.TemplateVo;
import com.group5.sebmmodels.dto.notification.TemplateDto;
import jakarta.servlet.http.HttpServletRequest;


/**
 * 通知模板服务接口
 */
public interface TemplateService {
    
    /**
     * 创建通知模板
     * @param createTemplateDto 创建模板请求DTO
     * @param request HTTP请求对象，用于获取当前用户信息
     * @return 创建的模板VO
     */
    TemplateVo createTemplate(CreateTemplateDto createTemplateDto, HttpServletRequest request);

    /**
     * 禁用模版
     * @param templateId 模版ID
     * @param request HTTP请求对象
     * @return 操作结果
     */
    Boolean disableTemplate(Long templateId, HttpServletRequest request);

    /**
     * 启用模版
     * @param templateId 模版ID
     * @param request HTTP请求对象
     * @return 操作结果
     */
    Boolean enableTemplate(Long templateId, HttpServletRequest request);

    /**
     * 更新通知模板
     * @param updateTemplateDto 更新模板请求DTO
     * @param request HTTP请求对象，用于获取当前用户信息
     * @return 更新后的模板VO
     */
    TemplateVo updateTemplate(UpdateTemplateDto updateTemplateDto, HttpServletRequest request);

    /**
     * 分页查询模板列表
     * @param templateQueryDto 查询条件
     * @return 分页结果
     */
    Page<TemplateVo> getTemplateList(TemplateQueryDto templateQueryDto);
    
    /**
     * 根据通知参数查询模板
     * @param notificationEvent 通知code
     * @return 模板DTO
     */
    TemplateDto findTemplateByParams(Integer notificationEvent);
}
