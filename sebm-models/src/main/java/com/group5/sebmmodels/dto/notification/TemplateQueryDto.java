package com.group5.sebmmodels.dto.notification;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模板列表查询请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateQueryDto {

    /**
     * 页码，从1开始
     */
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于等于1")
    private Integer pageNumber;

    /**
     * 每页条数
     */
    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页条数必须大于等于1")
    private Integer pageSize;

    /**
     * 模板标题（模糊查询）
     */
    private String templateTitle;

    /**
     * 通知节点
     */
    private Integer notificationNode;

    /**
     * 通知方式
     */
    private Integer notificationMethod;

    /**
     * 模板状态
     */
    private String status;

    /**
     * 创建者ID
     */
    private Long userId;
}
