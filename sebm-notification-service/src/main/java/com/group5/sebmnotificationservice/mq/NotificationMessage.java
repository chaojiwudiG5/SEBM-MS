package com.group5.sebmnotificationservice.mq;

import com.group5.sebmmodels.entity.TemplatePo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 通知消息实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationMessage implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * 消息ID
     */
    private String messageId;
    /**
     * 接收者id
     */
    private Long userId;
    /**
     * 模版信息
     */
    private TemplatePo template;
    
    /**
     * 模板变量
     */
    private Map<String, Object> templateVars;
    
    /**
     * 发送时间
     */
    private LocalDateTime sendTime;
    
    /**
     * 重试次数
     */
    private Integer retryCount = 0;
    
    /**
     * 最大重试次数
     */
    private Integer maxRetryCount = 3;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 通知任务ID（用于延时通知关联任务）
     */
    private Long taskId;
}
