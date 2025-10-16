package com.group5.sebmmodels.dto.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 更新通知模板请求DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTemplateDto {
    
    /**
     * 模板ID
     */
    @NotNull(message = "模板ID不能为空")
    private Long id;
    
    /**
     * 模板标题
     */
    @NotBlank(message = "模板标题不能为空")
    private String templateTitle;
    
    /**
     * 通知节点 (使用NotificationNodeEnum的code值)
     */
    @NotNull(message = "通知节点不能为空")
    private Integer notificationNode;

    /**
     * 通知方式 (使用NotificationMethodEnum的code值)
     */
    @NotNull(message = "通知方式不能为空")
    private List<Integer> notificationMethod;

    /**
     * 相关时间偏移（秒）
     */
    private Integer relateTimeOffset;

    /**
     * 内容
     */
    @NotBlank(message = "模板内容不能为空")
    private String content;

    /**
     * 通知角色(使用NotificationRoleEnum的code值)
     */
    private Integer notificationRole;

    /**
     * 通知code(使用NotificationEventEnum的code值)
     */
    private Integer notificationEvent;
    
    /**
     * 通知事件类型枚举(使用NotificationTypeEnum的code值)
     * 用于定义通知的时间偏移类型
     */
    private Integer notificationType;

    /**
     * 模版描述
     */
    private String templateDesc;
}
