package com.group5.sebmnotificationservice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.group5.sebmcommon.enums.NotificationMethodEnum;
import com.group5.sebmcommon.exception.BusinessException;
import com.group5.sebmcommon.exception.ErrorCode;
import com.group5.sebmcommon.exception.ThrowUtils;
import com.group5.sebmmodels.dto.notification.NotificationRecordQueryDto;
import com.group5.sebmmodels.vo.NotificationRecordVo;
import com.group5.sebmnotificationservice.dao.NotificationRecordMapper;
import com.group5.sebmmodels.entity.NotificationRecordPo;
import com.group5.sebmnotificationservice.enums.NotificationRecordStatusEnum;
import com.group5.sebmnotificationservice.service.NotificationRecordService;
import com.group5.sebmmodels.dto.notification.AdminNotificationQueryDto;
import com.group5.sebmmodels.entity.NotificationTaskPo;
import com.group5.sebmnotificationservice.dao.NotificationTaskMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    private final NotificationTaskMapper notificationTaskMapper;
    
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
        try {
            // 验证用户ID（必填）
            if (queryDto.getUserId() == null && queryDto.getQueryRole() == 1) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
            }

            // 构建查询条件
            QueryWrapper<NotificationRecordPo> queryWrapper = new QueryWrapper<>();

            queryWrapper.eq("status", NotificationRecordStatusEnum.SUCCESS.getCode())
                    .eq("isDelete", 0).eq("notificationMethod", 3);

            // 根据queryRole查询不同维度的数据
            if(queryDto.getQueryRole() == 0) {
                // 管理员角色：查询角色为管理员的所有通知
                // 先查询notificationRole为0的任务
                QueryWrapper<NotificationTaskPo> taskWrapper = new QueryWrapper<>();
                taskWrapper.eq("notificationRole", 0);
                List<NotificationTaskPo> adminTasks = notificationTaskMapper.selectList(taskWrapper);

                if (adminTasks.isEmpty()) {
                    // 如果没有管理员角色的通知任务，返回空结果
                    Page<NotificationRecordVo> emptyPage = new Page<>(queryDto.getPageNumber(), queryDto.getPageSize(), 0);
                    log.info("查询管理员通知记录成功，但没有找到管理员角色的通知任务");
                    return emptyPage;
                }

                // 获取所有管理员任务的ID
                List<Long> adminTaskIds = adminTasks.stream()
                        .map(NotificationTaskPo::getId)
                        .collect(Collectors.toList());

                // 查询这些任务的明细记录
                queryWrapper.in("notificationTaskId", adminTaskIds);

            } else if(queryDto.getQueryRole() == 1) {
                // 普通用户：查询该用户的通知
                queryWrapper.eq("userId", queryDto.getUserId());
            }

            // 增加已读状态查询
            if(queryDto.getReadStatus() != null){
                queryWrapper.eq("readStatus", queryDto.getReadStatus());
            }

            // 根据创建时间范围查询
            if (queryDto.getStartTime() != null) {
                LocalDateTime startDateTime = LocalDateTime.ofEpochSecond(
                        queryDto.getStartTime(), 0, java.time.ZoneOffset.ofHours(8)
                );
                queryWrapper.ge("createTime", startDateTime);
            }

            if (queryDto.getEndTime() != null) {
                LocalDateTime endDateTime = LocalDateTime.ofEpochSecond(
                        queryDto.getEndTime(), 0, java.time.ZoneOffset.ofHours(8)
                );
                queryWrapper.le("createTime", endDateTime);
            }

            // 按创建时间降序排列
            queryWrapper.orderByDesc("createTime");

            // 分页查询
            Page<NotificationRecordPo> page = new Page<>(queryDto.getPageNumber(), queryDto.getPageSize());
            Page<NotificationRecordPo> resultPage = this.page(page, queryWrapper);

            // 获取所有任务ID
            List<Long> taskIds = resultPage.getRecords().stream()
                    .map(NotificationRecordPo::getNotificationTaskId)
                    .distinct()
                    .collect(Collectors.toList());

            // 批量查询任务信息
            Map<Long, NotificationTaskPo> taskMap = new java.util.HashMap<>();
            if (!taskIds.isEmpty()) {
                List<NotificationTaskPo> tasks = notificationTaskMapper.selectBatchIds(taskIds);
                taskMap = tasks.stream().collect(Collectors.toMap(NotificationTaskPo::getId, t -> t));
            }

            // 转换为 VO
            Page<NotificationRecordVo> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
            Map<Long, NotificationTaskPo> finalTaskMap = taskMap;
            List<NotificationRecordVo> voList = resultPage.getRecords().stream()
                    .map(record -> convertToVo(record, finalTaskMap.get(record.getNotificationTaskId())))
                    .collect(Collectors.toList());
            voPage.setRecords(voList);

            // 如果有标题关键词，过滤结果
            if (StrUtil.isNotBlank(queryDto.getTitleKeyword())) {
                voList = voList.stream()
                        .filter(vo -> vo.getTitle() != null && vo.getTitle().contains(queryDto.getTitleKeyword()))
                        .collect(Collectors.toList());
                voPage.setRecords(voList);
                voPage.setTotal(voList.size());
            }

            log.info("查询通知记录成功: queryRole={}, userId={}, total={}, current={}, size={}",
                    queryDto.getQueryRole(), queryDto.getUserId(), voPage.getTotal(), voPage.getCurrent(), voPage.getSize());
            return voPage;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询通知记录时发生异常: queryDto={}, error={}", queryDto, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询通知记录失败");
        }
    }

    @Override
    public Page<NotificationRecordVo> queryAllSentNotifications(AdminNotificationQueryDto queryDto) {
        try {
            QueryWrapper<NotificationRecordPo> queryWrapper = new QueryWrapper<>();
            // 只查询发送成功
            queryWrapper.eq("status", NotificationRecordStatusEnum.SUCCESS.getCode());

            if (queryDto.getUserId() != null) {
                queryWrapper.eq("userId", queryDto.getUserId());
            }
//            if (queryDto.getIsDelete() != null) {
//                queryWrapper.eq("isDelete", queryDto.getIsDelete());
//            }
            if (queryDto.getReadStatus() != null) {
                if (queryDto.getReadStatus() == 0) {
                    queryWrapper.and(w -> w.eq("readStatus", 0).or().isNull("readStatus"));
                } else {
                    queryWrapper.eq("readStatus", queryDto.getReadStatus());
                }
            }

            // 时间范围
            if (queryDto.getStartTime() != null) {
                LocalDateTime startDateTime = LocalDateTime.ofEpochSecond(queryDto.getStartTime(), 0, java.time.ZoneOffset.ofHours(8));
                queryWrapper.ge("createTime", startDateTime);
            }
            if (queryDto.getEndTime() != null) {
                LocalDateTime endDateTime = LocalDateTime.ofEpochSecond(queryDto.getEndTime(), 0, java.time.ZoneOffset.ofHours(8));
                queryWrapper.le("createTime", endDateTime);
            }

            queryWrapper.orderByDesc("createTime");

            Page<NotificationRecordPo> page = new Page<>(queryDto.getPageNumber(), queryDto.getPageSize());
            Page<NotificationRecordPo> resultPage = this.page(page, queryWrapper);

            // 关联任务信息
            List<Long> taskIds = resultPage.getRecords().stream()
                    .map(NotificationRecordPo::getNotificationTaskId)
                    .distinct()
                    .collect(Collectors.toList());

            Map<Long, NotificationTaskPo> taskMap = new java.util.HashMap<>();
            if (!taskIds.isEmpty()) {
                List<NotificationTaskPo> tasks = notificationTaskMapper.selectBatchIds(taskIds);
                taskMap = tasks.stream().collect(Collectors.toMap(NotificationTaskPo::getId, t -> t));

                // 如果有角色过滤条件，先过滤任务
                if (queryDto.getNotificationRole() != null) {
                    Integer role = queryDto.getNotificationRole();
                    taskMap = taskMap.entrySet().stream()
                            .filter(entry -> role.equals(entry.getValue().getNotificationRole()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                }
            }

            // 转换VO并过滤
            Map<Long, NotificationTaskPo> finalTaskMap = taskMap;
            List<NotificationRecordVo> voList = resultPage.getRecords().stream()
                    .filter(record -> finalTaskMap.containsKey(record.getNotificationTaskId()))
                    .map(record -> convertToVo(record, finalTaskMap.get(record.getNotificationTaskId())))
                    .filter(vo -> {
                        // 标题关键词过滤
                        if (StrUtil.isNotBlank(queryDto.getTitleKeyword())) {
                            return vo.getTitle() != null && vo.getTitle().contains(queryDto.getTitleKeyword());
                        }
                        return true;
                    })
                    .collect(Collectors.toList());

            // 创建返回的分页对象，保持原始分页信息
            Page<NotificationRecordVo> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
            voPage.setRecords(voList);
            // 注意：不重新设置 total，保持数据库查询的总数

            log.info("管理员查询所有已发送通知成功: userId={}, isDelete={}, total={}, current={}, size={}",
                    queryDto.getUserId(), queryDto.getIsDelete(), voPage.getTotal(), voPage.getCurrent(), voPage.getSize());
            return voPage;
        } catch (Exception e) {
            log.error("管理员查询所有已发送通知时发生异常: queryDto={}, error={}", queryDto, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询通知记录失败");
        }
    }

    private NotificationRecordVo convertToVo(NotificationRecordPo record, NotificationTaskPo task) {
        // 如果 sendTime 为空，使用 updateTime 作为 sendTime
        LocalDateTime sendTime = record.getSendTime();
        if (sendTime == null) {
            sendTime = record.getUpdateTime();
        }
        
        return NotificationRecordVo.builder()
                .id(record.getId())
                .userId(record.getUserId())
                .title(task != null ? task.getTitle() : null)
                .content(task != null ? task.getContent() : null)
                .status(record.getStatus())
                .statusDesc(record.getStatus() != null ? NotificationRecordStatusEnum.getByCode(record.getStatus()).getDesc() : null)
                .readStatus(record.getReadStatus())
                .notificationMethod(record.getNotificationMethod())
                .notificationMethodDesc(record.getNotificationMethod() != null ? NotificationMethodEnum.parseMethod(record.getNotificationMethod()).getDescription() : null)
                .sendTime(sendTime)
                .createTime(record.getCreateTime())
                .build();
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
                    .readStatus(0) // 默认已读（发送成功后用户主要看站内/邮件，记录用于历史查询）
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
