package com.group5.sebmnotificationservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.group5.sebmcommon.annotation.AuthCheck;
import com.group5.sebmcommon.BaseResponse;
import com.group5.sebmcommon.ResultUtils;
import com.group5.sebmcommon.constant.NotificationConstant;
import com.group5.sebmcommon.enums.UserRoleEnum;
import com.group5.sebmcommon.exception.ErrorCode;
import com.group5.sebmcommon.exception.ThrowUtils;
import com.group5.sebmmodels.dto.notification.CreateTemplateDto;
import com.group5.sebmmodels.dto.notification.TemplateQueryDto;
import com.group5.sebmmodels.dto.notification.UpdateTemplateDto;
import com.group5.sebmmodels.vo.TemplateVo;
import com.group5.sebmnotificationservice.enums.NotificationMethodEnum;
import com.group5.sebmnotificationservice.enums.NotificationNodeEnum;
import com.group5.sebmnotificationservice.enums.NotificationRoleEnum;
import com.group5.sebmnotificationservice.service.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 通知模板控制器
 */
@Slf4j
@RestController
@Tag(name = "通知模板管理", description = "管理员通知模板相关接口")
@RequestMapping("/template")
@AllArgsConstructor
public class TemplateController {
    
    private final TemplateService templateService;
    
    /**
     * 创建通知模板 - 仅限管理员
     * @param createTemplateDto 创建模板请求DTO
     * @param request HTTP请求对象
     * @return 创建的模板信息
     */
    @PostMapping("/create")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    @Operation(summary = "创建通知模板", description = "管理员创建新的通知模板")
    public BaseResponse<TemplateVo> createTemplate(@RequestBody @Valid CreateTemplateDto createTemplateDto,
            HttpServletRequest request) {
        
        // 参数验证
        validateCreateTemplateParams(createTemplateDto);
        
        TemplateVo templateVo = templateService.createTemplate(createTemplateDto, request);
        log.info("createTemplate Dto: {}, Vo: {}", createTemplateDto, templateVo);
        
        return ResultUtils.success(templateVo);
    }

    /**
     * 禁用模版 - 仅限管理员
     * @param templateId 模版ID
     * @param request HTTP请求对象
     * @return 操作结果
     */
    @PostMapping("/disable/{templateId}")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    @Operation(summary = "禁用模版", description = "管理员禁用指定模版")
    public BaseResponse<Boolean> disableTemplate(@PathVariable Long templateId,
                                                HttpServletRequest request) {
        log.info("禁用模版，ID：{}", templateId);
        
        // 参数验证
        ThrowUtils.throwIf(templateId == null || templateId <= 0, ErrorCode.PARAMS_ERROR, "模版ID不能为空");
        
        Boolean result = templateService.disableTemplate(templateId, request);
        log.info("禁用模版结果：{}", result);
        
        return ResultUtils.success(result);
    }

    /**
     * 启用模版 - 仅限管理员
     * @param templateId 模版ID
     * @param request HTTP请求对象
     * @return 操作结果
     */
    @PostMapping("/enable/{templateId}")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    @Operation(summary = "启用模版", description = "管理员启用指定模版")
    public BaseResponse<Boolean> enableTemplate(@PathVariable Long templateId,
                                               HttpServletRequest request) {
        log.info("启用模版，ID：{}", templateId);
        
        // 参数验证
        ThrowUtils.throwIf(templateId == null || templateId <= 0, ErrorCode.PARAMS_ERROR, "模版ID不能为空");
        
        Boolean result = templateService.enableTemplate(templateId, request);
        log.info("启用模版结果：{}", result);
        
        return ResultUtils.success(result);
    }

    /**
     * 更新通知模板 - 仅限管理员
     * @param updateTemplateDto 更新模板请求DTO
     * @param request HTTP请求对象
     * @return 更新后的模板信息
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    @Operation(summary = "更新通知模板", description = "管理员更新现有通知模板")
    public BaseResponse<TemplateVo> updateTemplate(@RequestBody @Valid UpdateTemplateDto updateTemplateDto,
                                                  HttpServletRequest request) {
        log.info("更新模版，参数：{}", updateTemplateDto);
        
        // 参数验证
        validateUpdateTemplateParams(updateTemplateDto);
        
        TemplateVo templateVo = templateService.updateTemplate(updateTemplateDto, request);
        log.info("updateTemplate Dto: {}, Vo: {}", updateTemplateDto, templateVo);
        
        return ResultUtils.success(templateVo);
    }

    /**
     * 分页查询通知模板列表 - 仅限管理员
     * @param templateQueryDto 查询条件
     * @return 分页结果
     */
    @PostMapping("/list")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    @Operation(summary = "分页查询通知模板", description = "管理员分页查询通知模板列表")
    public BaseResponse<Page<TemplateVo>> getTemplateList(@RequestBody @Valid TemplateQueryDto templateQueryDto) {
        log.info("查询模板列表，参数：{}", templateQueryDto);
        
        Page<TemplateVo> templateVoPage = templateService.getTemplateList(templateQueryDto);
        
        return ResultUtils.success(templateVoPage);
    }
    
    /**
     * 根据ID查询模板详情 - 仅限管理员
     * @param templateId 模板ID
     * @return 模板详情
     */
    @GetMapping("/detail/{templateId}")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    @Operation(summary = "查询模板详情", description = "根据ID查询模板详细信息")
    public BaseResponse<TemplateVo> getTemplateDetail(@PathVariable Long templateId) {
        log.info("查询模板详情，ID：{}", templateId);
        
        // 这里需要添加一个根据ID查询VO的方法，或者直接使用现有的方法
        // 暂时返回null，实际项目中需要实现
        return ResultUtils.success(null);
    }
    
    /**
     * 验证创建模板的参数
     * @param createTemplateDto 创建模板请求DTO
     */
    private void validateCreateTemplateParams(CreateTemplateDto createTemplateDto) {
        // 验证通知节点是否有效
        ThrowUtils.throwIf(!NotificationNodeEnum.isValidCode(createTemplateDto.getNotificationNode()),
                ErrorCode.PARAMS_ERROR);

        // 验证通知节点是否有效
        ThrowUtils.throwIf(!NotificationRoleEnum.isValidCode(createTemplateDto.getNotificationRole()),
                ErrorCode.PARAMS_ERROR);
        
        // 验证模板标题长度
        ThrowUtils.throwIf(!StringUtils.hasText(createTemplateDto.getTemplateTitle()) || 
                createTemplateDto.getTemplateTitle().length() > NotificationConstant.MAX_TEMPLATE_TITLE_LENGTH,
                ErrorCode.PARAMS_ERROR);
        
        // 验证模板内容长度
        ThrowUtils.throwIf(!StringUtils.hasText(createTemplateDto.getContent()) || 
                createTemplateDto.getContent().length() > NotificationConstant.MAX_TEMPLATE_CONTENT_LENGTH,
                ErrorCode.PARAMS_ERROR);
        
        // 验证时间偏移量（如果不为空）
        if (createTemplateDto.getRelateTimeOffset() != null) {
            ThrowUtils.throwIf(createTemplateDto.getRelateTimeOffset() < NotificationConstant.MIN_TIME_OFFSET,
                    ErrorCode.PARAMS_ERROR);
            
            // 限制最大时间偏移量
            ThrowUtils.throwIf(createTemplateDto.getRelateTimeOffset() > NotificationConstant.MAX_TIME_OFFSET_SECONDS,
                    ErrorCode.PARAMS_ERROR);
        }

        // 验证传入的通知方式是否合法
        ThrowUtils.throwIf(!NotificationMethodEnum.isValidCode(createTemplateDto.getNotificationMethod()),
                ErrorCode.PARAMS_ERROR);
    }

    /**
     * 验证更新模板的参数
     * @param updateTemplateDto 更新模板请求DTO
     */
    private void validateUpdateTemplateParams(UpdateTemplateDto updateTemplateDto) {
        // 验证模版ID
        ThrowUtils.throwIf(updateTemplateDto.getId() == null || updateTemplateDto.getId() <= 0,
                ErrorCode.PARAMS_ERROR, "模版ID不能为空");
        
        // 验证通知节点是否有效
        ThrowUtils.throwIf(!NotificationNodeEnum.isValidCode(updateTemplateDto.getNotificationNode()),
                ErrorCode.PARAMS_ERROR, "通知节点无效");

        // 验证通知角色是否有效（如果提供）
        if (updateTemplateDto.getNotificationRole() != null) {
            ThrowUtils.throwIf(!NotificationRoleEnum.isValidCode(updateTemplateDto.getNotificationRole()),
                    ErrorCode.PARAMS_ERROR, "通知角色无效");
        }
        
        // 验证模板标题长度
        ThrowUtils.throwIf(!StringUtils.hasText(updateTemplateDto.getTemplateTitle()) || 
                updateTemplateDto.getTemplateTitle().length() > NotificationConstant.MAX_TEMPLATE_TITLE_LENGTH,
                ErrorCode.PARAMS_ERROR, "模板标题长度无效");
        
        // 验证模板内容长度
        ThrowUtils.throwIf(!StringUtils.hasText(updateTemplateDto.getContent()) || 
                updateTemplateDto.getContent().length() > NotificationConstant.MAX_TEMPLATE_CONTENT_LENGTH,
                ErrorCode.PARAMS_ERROR, "模板内容长度无效");
        
        // 验证时间偏移量（如果不为空）
        if (updateTemplateDto.getRelateTimeOffset() != null) {
            ThrowUtils.throwIf(updateTemplateDto.getRelateTimeOffset() < NotificationConstant.MIN_TIME_OFFSET,
                    ErrorCode.PARAMS_ERROR, "时间偏移量不能小于0");
            
            // 限制最大时间偏移量
            ThrowUtils.throwIf(updateTemplateDto.getRelateTimeOffset() > NotificationConstant.MAX_TIME_OFFSET_SECONDS,
                    ErrorCode.PARAMS_ERROR, "时间偏移量不能超过7天");
        }

        // 验证传入的通知方式是否合法
        ThrowUtils.throwIf(!NotificationMethodEnum.isValidCode(updateTemplateDto.getNotificationMethod()),
                ErrorCode.PARAMS_ERROR, "通知方式无效");
    }
}
