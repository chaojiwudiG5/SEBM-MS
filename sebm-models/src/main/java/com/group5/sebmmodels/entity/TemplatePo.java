package com.group5.sebmmodels.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知模板实体类
 */
@Data
@TableName("notificationTemplate")
public class TemplatePo {
    
    /**
     * 模板ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 模板标题
     */
    @TableField("templateTitle")
    private String templateTitle;
    
    /**
     * 通知方式
     */
    @TableField("notificationMethod")
    private List<Integer> notificationMethod;
    
    /**
     * 通知节点
     */
    @TableField("notificationNode")
    private String notificationNode;

    /**
     * 通知角色
     */
    @TableField("notificationRole")
    private String notificationRole;

    /**
     * 通知类型
     */
    @TableField("notificationType")
    private Integer notificationType;

    /**
     * 通知事件描述（用户自定义填写）
     */
    @TableField("notificationEvent")
    private Integer notificationEvent;
    
    /**
     * 相关时间偏移
     */
    @TableField("relateTimeOffset")
    private Long relateTimeOffset;
    
    /**
     * 用户ID
     */
    @TableField("userId")
    private Long userId;
    
    /**
     * 内容
     */
    @TableField("templateContent")
    private String templateContent;

    /**
     * 模版描述
     */
    @TableField("templateDesc")
    private String templateDesc;
    
    /**
     * todo 暂时未启用模版禁用功能，所以这个字段暂时没用
     * 状态
     */
    @TableField("status")
    private Integer status;
    
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
