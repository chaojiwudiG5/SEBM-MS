package com.group5.sebmmodels.entity;

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
 * 通知记录实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("notificationRecord")
public class NotificationRecordPo {
    
    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    @TableField("userId")
    private Long userId;
    
    /**
     * 通知标题
     */
    @TableField("title")
    private String title;
    
    /**
     * 通知内容
     */
    @TableField("content")
    private String content;
    
    /**
     * 发送状态 (0-待发送, 1-发送成功, 2-发送失败)
     */
    @TableField("status")
    private Integer status;

    /**
     * 已读状态 (0-未读, 1-已读)
     */
    @TableField("readStatus")
    private Integer readStatus;
    
    /**
     * 发送时间
     */
    @TableField("sendTime")
    private LocalDateTime sendTime;
    
    /**
     * 是否删除 (0-未删除, 1-已删除)
     */
    @TableField("isDelete")
    private Integer isDelete;
    
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

