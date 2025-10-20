package com.group5.sebmmodels.dto.notification;

import com.group5.sebmmodels.dto.common.PageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 管理员通知查询 DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AdminNotificationQueryDto extends PageDto {

    private Long userId;

    /**
     * 是否删除标记：0-未删除，1-已删除
     */
    private Integer isDelete;

    /**
     * 已读状态：0-未读，1-已读
     */
    private Integer readStatus;

    /**
     * 标题关键词
     */
    private String titleKeyword;

    /**
     * 通知角色 (0-管理员, 1-用户, 2-技工)
     */
    private Integer notificationRole;

    /**
     * 秒级开始时间戳
     */
    private Long startTime;

    /**
     * 秒级结束时间戳
     */
    private Long endTime;
}
