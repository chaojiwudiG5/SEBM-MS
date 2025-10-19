package com.group5.sebmmodels.dto.notification;

import com.group5.sebmmodels.dto.common.PageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 通知记录查询 DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRecordQueryDto extends PageDto {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 标题关键词（可选）
     */
    private String titleKeyword;

    /**
     * 查询角色，0-管理员，1-用户
     */
    private Integer queryRole;

    /**
     * 已读状态，0-未读，1-已读
     */
    private Integer readStatus;

    /**
     * 秒级时间戳
     */
    private Long startTime;

    /**
     * 秒级时间戳
     */
    private Long endTime;
}

