package com.group5.sebmnotificationservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知记录表实体类（明细表）
 * 记录每个用户收到的每种通知方式及其状态
 * 一个通知任务可以发给多个用户，每个用户可以通过多种方式接收
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("notificationRecord")
public class NotificationRecordPo {
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 通知任务ID（关联 notificationTask 表）
     */
    @TableField("notificationTaskId")
    private Long notificationTaskId;
    
    /**
     * 用户ID（接收通知的用户）
     */
    @TableField("userId")
    private Long userId;
    
    /**
     * 通知方式 (1-邮件, 2-短信, 3-站内信)
     */
    @TableField("notificationMethod")
    private Integer notificationMethod;
    
    /**
     * 发送状态 (0-未发送, 1-发送成功, 2-发送失败)
     */
    @TableField("status")
    private Integer status;
    
    /**
     * 已读状态 (0-未读, 1-已读)
     */
    @TableField("readStatus")
    private Integer readStatus;
    
    /**
     * 是否删除 (0-未删除, 1-已删除)
     */
    @TableField("isDelete")
    private Integer isDelete;
    
    /**
     * 发送时间
     */
    @TableField("sendTime")
    private LocalDateTime sendTime;
    
    /**
     * 失败原因或错误信息
     */
    @TableField("errorMsg")
    private String errorMsg;
    
    /**
     * 创建时间
     */
    @TableField("createTime")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField("updateTime")
    private LocalDateTime updateTime;
}

