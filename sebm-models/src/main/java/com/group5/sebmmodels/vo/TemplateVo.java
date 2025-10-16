package com.group5.sebmmodels.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知模板响应VO
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TemplateVo {
    
    /**
     * 模板ID
     */
    private Long id;
    
    /**
     * 模板标题
     */
    private String templateTitle;
    
    /**
     * 通知节点
     */
    private String notificationNode;
    
    /**
     * 通知方式
     */
    private List<Integer> notificationMethod;
    
    /**
     * 通知描述
     */
    private String templateDesc;

    /**
     * 通知角色
     */
    private String notificationRole;

    /**
     * 通知类型
     */
    private Integer notificationType;

    /**
     * 通知事件
     */
    private Integer notificationEvent;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

