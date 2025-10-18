package com.group5.sebmnotificationservice.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
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
 * 通知任务表实体类
 * 描述通知的基本信息，不涉及具体用户
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("notificationTask")
public class NotificationTaskPo {
    
    /**
     * 任务ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 通知标题
     */
    @TableField("title")
    private String title;
    
    /**
     * 通知内容
     */
    @TableField(value = "content", insertStrategy = FieldStrategy.ALWAYS)
    private String content;
    
    /**
     * 通知角色 (0-管理员, 1-用户, 2-技工)
     */
    @TableField("notificationRole")
    private Integer notificationRole;
    
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

