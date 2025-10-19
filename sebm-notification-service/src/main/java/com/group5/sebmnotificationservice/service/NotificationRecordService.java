package com.group5.sebmnotificationservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.group5.sebmmodels.dto.notification.NotificationRecordQueryDto;
import com.group5.sebmmodels.vo.NotificationRecordVo;
import com.group5.sebmmodels.entity.NotificationRecordPo;

import java.util.List;

/**
 * 通知记录服务接口
 */
public interface NotificationRecordService extends IService<NotificationRecordPo> {
    
    /**
     * 保存通知记录
     * @param userId 用户ID
     * @param title 通知标题
     * @param content 通知内容
     * @param status 发送状态
     * @return 是否保存成功
     */
    boolean saveNotificationRecord(Long userId, String title, String content, Integer status);
    
    /**
     * 创建通知记录并返回记录ID
     * @param userId 用户ID
     * @param title 通知标题
     * @param content 通知内容
     * @param status 发送状态
     * @return 记录ID，失败返回null
     */
    Long createNotificationRecord(Long userId, String title, String content, Integer status);
    
    /**
     * 更新通知记录状态
     * @param recordId 记录ID
     * @param status 发送状态
     * @return 是否更新成功
     */
    boolean updateRecordStatus(Long recordId, Integer status);

    /**
     * 分页查询通知记录
     * @param queryDto 查询条件
     * @return 通知记录分页数据
     */
    Page<NotificationRecordVo> queryNotificationRecords(NotificationRecordQueryDto queryDto);

    /**
     * 删除单个通知记录（软删除）
     * @param id 记录ID
     * @return 是否删除成功
     */
    boolean deleteNotificationRecord(Long id);

    /**
     * 批量删除通知记录（软删除）
     * @param ids 记录ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteNotificationRecords(List<Long> ids);

    /**
     * 清空用户所有已读消息（软删除）
     * @param userId 用户ID
     * @return 是否清空成功
     */
    boolean clearUserNotifications(Long userId);

    /**
     * 标记单条消息为已读
     * @param recordId 记录ID
     * @return 是否标记成功
     */
    boolean markAsRead(Long recordId);

    /**
     * 批量标记消息为已读
     * @param ids 记录ID列表
     * @return 是否标记成功
     */
    boolean batchMarkAsRead(List<Long> ids);


    /**
     * 标记用户所有未读消息为已读（需要权限校验）
     * @param userId 用户ID
     * @param userRole 用户角色
     * @return 是否标记成功
     */
    boolean markAllAsRead(Long userId, Integer userRole);

    /**
     * 创建通知记录（新版本 - 任务记录分离）
     * @param notificationTaskId 通知任务ID
     * @param userId 用户ID
     * @param notificationMethod 通知方式
     * @param status 发送状态
     * @return 是否创建成功
     */
    boolean createRecord(Long notificationTaskId, Long userId, Integer notificationMethod, Integer status);

}


