package com.group5.sebmmodels.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 模板数据传输对象
 * 用于Service层之间的数据传输
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateDto {
    
    /**
     * 模板ID
     */
    private Long id;
    
    /**
     * 模板标题
     */
    private String templateTitle;
    
    /**
     * 模板类型
     */
    private List<Integer> notificationMethod;
    
    /**
     * 通知节点
     */
    private String notificationNode;

    /**
     * 通知角色
     */
    private String notificationRole;

    /**
     * 通知类型
     */
    private Integer notificationType;

    /**
     * 通知事件类型（使用NotificationEventEnum的code值）
     */
    private Integer notificationEvent;
    
    /**
     * 相关时间偏移
     */
    private Long relateTimeOffset;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 内容
     */
    private String content;

    /**
     * 模版描述
     */
    private String templateDesc;
    
    /**
     * 状态 (0-禁用, 1-启用)
     */
    private Integer status;
    
    /**
     * 是否删除 (0-未删除, 1-已删除)
     */
    private Integer isDelete;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
