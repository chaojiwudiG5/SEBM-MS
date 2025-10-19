package com.group5.sebmnotificationservice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.group5.sebmcommon.exception.BusinessException;
import com.group5.sebmcommon.exception.ErrorCode;
import com.group5.sebmcommon.exception.ThrowUtils;
import com.group5.sebmmodels.dto.notification.NotificationRecordQueryDto;
import com.group5.sebmmodels.vo.NotificationRecordVo;
import com.group5.sebmnotificationservice.dao.NotificationRecordMapper;
import com.group5.sebmmodels.entity.NotificationRecordPo;
import com.group5.sebmnotificationservice.enums.NotificationRecordStatusEnum;
import com.group5.sebmnotificationservice.service.NotificationRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知记录服务实现类（新版本 - 任务记录分离）
 */
@Slf4j
@Service
@AllArgsConstructor
public class NotificationRecordServiceImpl extends ServiceImpl<NotificationRecordMapper, NotificationRecordPo> 
        implements NotificationRecordService {

    private final NotificationRecordMapper notificationRecordMapper;
    
    @Override
    public boolean saveNotificationRecord(Long userId, String title, String content, Integer status) {
        // 旧版本方法，暂时保留以保持兼容性
        log.warn("调用了旧版本的 saveNotificationRecord 方法，建议使用新版本的 createRecord 方法");
        return false;
    }

    @Override
    public Long createNotificationRecord(Long userId, String title, String content, Integer status) {
        // 旧版本方法，暂时保留以保持兼容性
        log.warn("调用了旧版本的 createNotificationRecord 方法，建议使用新版本的 createRecord 方法");
        return null;
    }

    @Override
    public boolean updateRecordStatus(Long recordId, Integer status) {
        try {
            UpdateWrapper<NotificationRecordPo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", recordId)
                    .set("status", status)
                    .set("updateTime", LocalDateTime.now());

            if (status == NotificationRecordStatusEnum.SUCCESS.getCode()) {
                updateWrapper.set("sendTime", LocalDateTime.now());
            }

            boolean result = this.update(updateWrapper);

            if (result) {
                log.info("通知记录状态更新成功: recordId={}, status={}", recordId, status);
            } else {
                log.error("通知记录状态更新失败: recordId={}", recordId);
            }

            return result;
        } catch (Exception e) {
            log.error("更新通知记录状态时发生异常: recordId={}, error={}", recordId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Page<NotificationRecordVo> queryNotificationRecords(NotificationRecordQueryDto queryDto) {
        // 简化实现，具体逻辑根据需要补充
        Page<NotificationRecordPo> page = new Page<>(queryDto.getPageNumber(), queryDto.getPageSize());
        QueryWrapper<NotificationRecordPo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", queryDto.getUserId())
                .eq("isDelete", 0)
                .orderByDesc("createTime");
        
        Page<NotificationRecordPo> poPage = this.page(page, queryWrapper);
        
        Page<NotificationRecordVo> voPage = new Page<>(poPage.getCurrent(), poPage.getSize(), poPage.getTotal());
        // VO 转换需要根据实际需求实现
        return voPage;
    }

    @Override
    public boolean deleteNotificationRecord(Long id) {
        try {
            UpdateWrapper<NotificationRecordPo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", id)
                    .set("isDelete", 1)
                    .set("updateTime", LocalDateTime.now());

            return this.update(updateWrapper);
        } catch (Exception e) {
            log.error("删除通知记录时发生异常: id={}, error={}", id, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean batchDeleteNotificationRecords(List<Long> ids) {
        try {
            UpdateWrapper<NotificationRecordPo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("id", ids)
                    .set("isDelete", 1)
                    .set("updateTime", LocalDateTime.now());

            return this.update(updateWrapper);
        } catch (Exception e) {
            log.error("批量删除通知记录时发生异常: ids={}, error={}", ids, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean clearUserNotifications(Long userId) {
        try {
            UpdateWrapper<NotificationRecordPo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("userId", userId)
                    .eq("readStatus", 1)
                    .set("isDelete", 1)
                    .set("updateTime", LocalDateTime.now());

            return this.update(updateWrapper);
        } catch (Exception e) {
            log.error("清空用户通知时发生异常: userId={}, error={}", userId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean markAsRead(Long recordId) {
        // 1. 查询通知记录
        NotificationRecordPo record = this.getById(recordId);
        ThrowUtils.throwIf(record == null, ErrorCode.NOT_FOUND_ERROR, "通知记录不存在");
        
        // 2. 检查是否已删除
        ThrowUtils.throwIf(record.getIsDelete() == 1, ErrorCode.OPERATION_ERROR, "通知记录已删除");
        
        // 3. 检查是否已读（避免重复更新）
        if (record.getReadStatus() == 1) {
            log.info("通知记录已是已读状态: recordId={}", recordId);
            return true;
        }
        
        // 4. 更新已读状态
        record.setReadStatus(1);
        record.setUpdateTime(LocalDateTime.now());
        boolean result = this.updateById(record);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "标记消息为已读失败");
        
        log.info("标记消息为已读成功: recordId={}", recordId);
        return true;
    }

    @Override
    public boolean batchMarkAsRead(List<Long> ids) {
        try {
            UpdateWrapper<NotificationRecordPo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("id", ids)
                    .set("readStatus", 1)
                    .set("updateTime", LocalDateTime.now());

            return this.update(updateWrapper);
        } catch (Exception e) {
            log.error("批量标记已读时发生异常: ids={}, error={}", ids, e.getMessage(), e);
            return false;
        }
    }


    @Override
    public boolean markAllAsRead(Long userId, Integer userRole) {
        if (userRole == null) {
            // 如果前端没有传入，则默认普通用户
            userRole = 1;
        }
        
        // 如果是管理员（userRole=1），则标记所有发给管理员角色的通知为已读
        if (userRole == 1) {
            // 通过关联查询任务表来筛选出所属角色，标记所有发给管理员角色的通知为已读
            int affectedRows = notificationRecordMapper.markAtlAsReadByRole(0);
            log.info("管理员标记所有发给管理员角色的未读消息为已读: affectedRows={}", affectedRows);
            return affectedRows > 0;
        } else {
            // 普通用户或技工：按userId标记该用户的所有未读消息
            UpdateWrapper<NotificationRecordPo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("userId", userId)
                    .eq("isDelete", 0)
                    .eq("readStatus", 0)
                    .set("readStatus", 1)
                    .set("updateTime", LocalDateTime.now());
            
            boolean result = this.update(updateWrapper);
            
            if (result) {
                log.info("用户标记自己的未读消息为已读成功: userId={}, userRole={}", userId, userRole);
            } else {
                log.warn("用户标记自己的未读消息为已读失败或无未读消息: userId={}", userId);
            }
            
            return result;
        }
    }

    @Override
    public boolean createRecord(Long notificationTaskId, Long userId, Integer notificationMethod, Integer status) {
        try {
            NotificationRecordPo record = NotificationRecordPo.builder()
                    .notificationTaskId(notificationTaskId)
                    .userId(userId)
                    .notificationMethod(notificationMethod)
                    .status(status)
                    .readStatus(0) // 默认未读
                    .sendTime(LocalDateTime.now())
                    .isDelete(0)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();

            boolean result = this.save(record);

            if (result) {
                log.info("通知记录创建成功: taskId={}, userId={}, method={}, status={}", 
                        notificationTaskId, userId, notificationMethod, status);
            } else {
                log.error("通知记录创建失败: taskId={}, userId={}", notificationTaskId, userId);
            }

            return result;
        } catch (Exception e) {
            log.error("创建通知记录时发生异常: taskId={}, userId={}, error={}", 
                    notificationTaskId, userId, e.getMessage(), e);
            return false;
        }
    }
}
