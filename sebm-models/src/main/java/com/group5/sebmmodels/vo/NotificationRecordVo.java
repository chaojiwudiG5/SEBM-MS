package com.group5.sebmmodels.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知记录 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRecordVo {
    
    /**
     * 记录ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 通知标题
     */
    private String title;
    
    /**
     * 通知内容
     */
    private String content;
    
    /**
     * 发送状态 (0-待发送, 1-发送成功, 2-发送失败)
     */
    private Integer status;
    
    /**
     * 状态描述
     */
    private String statusDesc;
    
    /**
     * 已读状态 (0-未读, 1-已读)
     */
    private Integer readStatus;
    
    /**
     * 通知方式 (1-邮件, 2-短信, 3-站内信)
     */
    private Integer notificationMethod;
    
    /**
     * 通知方式描述
     */
    private String notificationMethodDesc;
    
    /**
     * 发送时间
     */
    private LocalDateTime sendTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

