package com.group5.sebmnotificationservice.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.group5.sebmmodels.entity.NotificationRecordPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知记录 Mapper
 */
@Mapper
public interface NotificationRecordMapper extends BaseMapper<NotificationRecordPo> {

    /**
     * 查询用户的未读记录数量
     * @param userId 用户ID
     * @return 未读数量
     */
    Long countUnreadByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户的所有未读记录
     * @param userId 用户ID
     * @return 未读记录列表
     */
    List<NotificationRecordPo> selectUnreadByUserId(@Param("userId") Long userId);
    
    /**
     * 查询通知任务的所有记录
     * @param notificationTaskId 通知任务ID
     * @return 记录列表
     */
    List<NotificationRecordPo> selectByTaskId(@Param("notificationTaskId") Long notificationTaskId);

    /**
     * 根据通知角色标记所有未读消息为已读
     * @param notificationRole 通知角色 (0-管理员, 1-用户, 2-技工)
     * @return 影响的行数
     */
    int markAtlAsReadByRole(@Param("notificationRole") Integer notificationRole);

}
