package com.group5.sebmnotificationservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.group5.sebmcommon.constant.NotificationConstant;
import com.group5.sebmcommon.exception.ErrorCode;
import com.group5.sebmcommon.exception.ThrowUtils;
import com.group5.sebmmodels.dto.notification.CreateTemplateDto;
import com.group5.sebmmodels.dto.notification.TemplateQueryDto;
import com.group5.sebmmodels.dto.notification.UpdateTemplateDto;
import com.group5.sebmmodels.vo.TemplateVo;
import com.group5.sebmnotificationservice.converter.TemplateConverter;
import com.group5.sebmnotificationservice.dao.TemplateMapper;
import com.group5.sebmmodels.entity.TemplatePo;
import com.group5.sebmnotificationservice.enums.NotificationEventEnum;
import com.group5.sebmnotificationservice.service.TemplateService;
import com.group5.sebmmodels.dto.notification.TemplateDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 通知模板服务实现类
 */
@Slf4j
@Service
public class TemplateServiceImpl extends ServiceImpl<TemplateMapper, TemplatePo> implements TemplateService {
    
    @Autowired
    private TemplateConverter templateConverter;

    /**
     * 创建通知模板
     * @param createTemplateDto 创建模板请求DTO
     * @param request HTTP请求对象
     * @return 创建的模板VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TemplateVo createTemplate(CreateTemplateDto createTemplateDto, HttpServletRequest request) {
        log.info("1.before createTemplate execute，params：{}", createTemplateDto);

        // DTO 转 PO
        TemplatePo templatePo = templateConverter.toPo(createTemplateDto, request);
        
        // 插入数据库
        boolean insertResult = this.save(templatePo);
        ThrowUtils.throwIf(!insertResult, ErrorCode.OPERATION_ERROR, "模板创建失败");
        
        log.info("2.create template success, template id：{}, notification event: {}",
                templatePo.getId(), createTemplateDto.getNotificationEvent());
        
        // PO 转 VO 并返回
        return templateConverter.toVo(templatePo);
    }

    /**
     * 禁用模版
     * @param templateId 模版ID
     * @param request HTTP请求对象
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean disableTemplate(Long templateId, HttpServletRequest request) {
        log.info("禁用模版，ID：{}", templateId);
        
        // 1. 检查模版是否存在
        TemplatePo templatePo = this.getById(templateId);
        ThrowUtils.throwIf(templatePo == null, ErrorCode.NOT_FOUND_ERROR, "模版不存在");
        
        // 2. 检查模版是否已经被禁用
        ThrowUtils.throwIf(NotificationConstant.TEMPLATE_STATUS_DISABLED.equals(templatePo.getStatus()), 
                ErrorCode.OPERATION_ERROR, "模版已经被禁用");
        
        // 3. 创建更新对象，只更新状态和时间，避免更新JSON字段
        TemplatePo updatePo = new TemplatePo();
        updatePo.setId(templateId);
        updatePo.setStatus(NotificationConstant.TEMPLATE_STATUS_DISABLED);
        updatePo.setUpdateTime(LocalDateTime.now());
        
        // 4. 保存更新
        boolean updateResult = this.updateById(updatePo);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "模版禁用失败");
        
        log.info("模版禁用成功，ID：{}", templateId);
        return true;
    }

    /**
     * 启用模版
     * @param templateId 模版ID
     * @param request HTTP请求对象
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean enableTemplate(Long templateId, HttpServletRequest request) {
        log.info("启用模版，ID：{}", templateId);
        
        // 1. 检查模版是否存在
        TemplatePo templatePo = this.getById(templateId);
        ThrowUtils.throwIf(templatePo == null, ErrorCode.NOT_FOUND_ERROR, "模版不存在");
        
        // 2. 检查模版是否已经被启用
        ThrowUtils.throwIf(NotificationConstant.TEMPLATE_STATUS_ACTIVE.equals(templatePo.getStatus()), 
                ErrorCode.OPERATION_ERROR, "模版已经是启用状态");
        
        // 3. 创建更新对象，只更新状态和时间
        TemplatePo updatePo = new TemplatePo();
        updatePo.setId(templateId);
        updatePo.setStatus(NotificationConstant.TEMPLATE_STATUS_ACTIVE);
        updatePo.setUpdateTime(LocalDateTime.now());
        
        // 4. 保存更新
        boolean updateResult = this.updateById(updatePo);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "模版启用失败");
        
        log.info("模版启用成功，ID：{}", templateId);
        return true;
    }

    /**
     * 更新通知模板
     * @param updateTemplateDto 更新模板请求DTO
     * @param request HTTP请求对象
     * @return 更新后的模板VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TemplateVo updateTemplate(UpdateTemplateDto updateTemplateDto, HttpServletRequest request) {
        log.info("更新模版，参数：{}", updateTemplateDto);
        
        // 1. 检查模版是否存在
        TemplatePo existingTemplate = this.getById(updateTemplateDto.getId());
        ThrowUtils.throwIf(existingTemplate == null, ErrorCode.NOT_FOUND_ERROR, "模版不存在");
        
        // 2. 检查模版是否已被删除
        ThrowUtils.throwIf(NotificationConstant.NOT_DELETED != existingTemplate.getIsDelete(), 
                ErrorCode.NOT_FOUND_ERROR, "模版已被删除");
        
        // 3. DTO 转 PO，只更新需要更新的字段
        TemplatePo updatePo = new TemplatePo();
        updatePo.setId(updateTemplateDto.getId());
        updatePo.setTemplateTitle(updateTemplateDto.getTemplateTitle());
        updatePo.setNotificationMethod(updateTemplateDto.getNotificationMethod());
        updatePo.setNotificationNode(updateTemplateDto.getNotificationNode().toString());
        updatePo.setNotificationRole(updateTemplateDto.getNotificationRole() != null ? 
                updateTemplateDto.getNotificationRole().toString() : null);
        updatePo.setNotificationType(updateTemplateDto.getNotificationType());
        updatePo.setNotificationEvent(updateTemplateDto.getNotificationEvent());
        updatePo.setRelateTimeOffset(updateTemplateDto.getRelateTimeOffset() != null ? 
                updateTemplateDto.getRelateTimeOffset().longValue() : null);
        updatePo.setTemplateContent(updateTemplateDto.getContent());
        updatePo.setTemplateDesc(updateTemplateDto.getTemplateDesc());
        updatePo.setUpdateTime(LocalDateTime.now());
        
        // 4. 执行更新
        boolean updateResult = this.updateById(updatePo);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "模版更新失败");
        
        log.info("模版更新成功，ID：{}", updateTemplateDto.getId());
        
        // 5. 查询更新后的模版并转换为VO
        TemplatePo updatedTemplate = this.getById(updateTemplateDto.getId());
        return templateConverter.toVo(updatedTemplate);
    }

    /**
     * 分页查询模板列表
     * @param templateQueryDto 查询条件
     * @return 分页结果
     */
    @Override
    public Page<TemplateVo> getTemplateList(TemplateQueryDto templateQueryDto) {
        log.info("查询模板列表，参数：{}", templateQueryDto);
        
        // 1. 创建分页对象
        Page<TemplatePo> page = new Page<>(templateQueryDto.getPageNumber(), templateQueryDto.getPageSize());
        
        // 2. 构建查询条件
        QueryWrapper<TemplatePo> queryWrapper = buildQueryWrapper(templateQueryDto);
        
        // 3. 执行分页查询
        Page<TemplatePo> templatePage = this.page(page, queryWrapper);
        
        // 4. 将 PO 转 VO
        List<TemplateVo> voList = templatePage.getRecords().stream()
                .map(templateConverter::toVo)
                .collect(Collectors.toList());
        
        // 5. 构建返回结果
        Page<TemplateVo> resultPage = new Page<>(templatePage.getCurrent(), templatePage.getSize(), templatePage.getTotal());
        resultPage.setRecords(voList);
        
        log.info("查询模板列表完成，总数：{}，当前页：{}", templatePage.getTotal(), templatePage.getCurrent());
        return resultPage;
    }
    
    /**
     * 根据通知参数查询模板
     * @param notificationEvent 通知code
     * @return 模板DTO
     */
    @Override
    public TemplateDto findTemplateByParams(Integer notificationEvent) {
        log.info("查询默认模板 - 通知code: {}", notificationEvent);

//
//        QueryWrapper<TemplatePo> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("notificationEvent", notificationEvent)
//                .eq("isDelete", NotificationConstant.NOT_DELETED)
//                .eq("status", NotificationConstant.TEMPLATE_STATUS_ACTIVE);
//
//        queryWrapper.orderByDesc("createTime")
//                .last("LIMIT 1");
//
//        TemplatePo template = this.getOne(queryWrapper);
//
//        if (template == null) {
//            log.warn("未找到通知 {} 的模板", notificationEvent);
//            return null;
//        }
//        log.info("找到默认模板: ID={}, 标题={}", template.getId(), template.getTemplateTitle());
//        // PO 转 DTO
//        return templateConverter.toDto(template);

        // 写死返回值，用于测试
        TemplateDto templateDto = new TemplateDto();
        templateDto.setId(1L);
        templateDto.setTemplateTitle("测试模板");
        templateDto.setNotificationMethod(List.of(1, 3)); // 邮件和站内信
        templateDto.setNotificationNode("BORROW_REQUEST_SUCCESS");
        templateDto.setNotificationRole("Borrower");
        templateDto.setNotificationType(0); // 即时通知
        templateDto.setNotificationEvent(NotificationEventEnum.BORROW_APPLICATION_APPROVED.getCode());
        templateDto.setRelateTimeOffset(0L);
        templateDto.setContent("您的设备租借已成功，租借时长两小时");
        templateDto.setTemplateDesc("测试模板描述");
        
        log.info("返回写死的模板: ID={}, 标题={}", templateDto.getId(), templateDto.getTemplateTitle());
        return templateDto;
    }
    
    /**
     * 构建查询条件
     * @param templateQueryDto 查询参数
     * @return QueryWrapper
     */
    private QueryWrapper<TemplatePo> buildQueryWrapper(TemplateQueryDto templateQueryDto) {
        QueryWrapper<TemplatePo> queryWrapper = new QueryWrapper<>();
        
        // 基础条件：未删除
        queryWrapper.eq("isDelete", NotificationConstant.NOT_DELETED);
        
        // 模板标题模糊查询
        if (StringUtils.hasText(templateQueryDto.getTemplateTitle())) {
            queryWrapper.like("templateTitle", templateQueryDto.getTemplateTitle());
        }
        
        // 通知节点
        if (templateQueryDto.getNotificationNode() != null) {
            queryWrapper.eq("notificationNode", templateQueryDto.getNotificationNode());
        }
        
        // 通知方式
        if (templateQueryDto.getNotificationMethod() != null) {
            queryWrapper.eq("notificationMethod", templateQueryDto.getNotificationMethod());
        }
        
        // 通知事件
        if (Objects.nonNull(templateQueryDto.getNotificationEvent())) {
            queryWrapper.like("notificationEvent", templateQueryDto.getNotificationEvent());
        }
        
        // 通知类型
        if (templateQueryDto.getNotificationType() != null) {
            queryWrapper.eq("notificationType", templateQueryDto.getNotificationType());
        }
        
        // 通知角色
        if (templateQueryDto.getNotificationRole() != null) {
            queryWrapper.eq("notificationRole", templateQueryDto.getNotificationRole().toString());
        }
        
        // 创建者ID
        if (templateQueryDto.getUserId() != null) {
            queryWrapper.eq("userId", templateQueryDto.getUserId());
        }

        // 状态
        if (templateQueryDto.getStatus() != null) {
            queryWrapper.eq("status", templateQueryDto.getStatus());
        }
        
        return queryWrapper;
    }
}
