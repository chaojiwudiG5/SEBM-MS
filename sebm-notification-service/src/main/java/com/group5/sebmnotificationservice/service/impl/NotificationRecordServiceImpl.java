package com.group5.sebmnotificationservice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.group5.sebmcommon.exception.BusinessException;
import com.group5.sebmcommon.exception.ErrorCode;
import com.group5.sebmmodels.dto.notification.NotificationRecordQueryDto;
import com.group5.sebmmodels.vo.NotificationRecordVo;
import com.group5.sebmnotificationservice.dao.NotificationRecordMapper;
import com.group5.sebmmodels.entity.NotificationRecordPo;
import com.group5.sebmnotificationservice.enums.NotificationRecordStatusEnum;
import com.group5.sebmnotificationservice.service.NotificationRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知记录服务实现类
 */
@Slf4j
@Service
public class NotificationRecordServiceImpl extends ServiceImpl<NotificationRecordMapper, NotificationRecordPo> 
        implements NotificationRecordService {
    
    @Override
    public boolean saveNotificationRecord(Long userId, String title, String content, Integer status) {
        try {
            NotificationRecordPo record = NotificationRecordPo.builder()
                    .userId(userId)
                    .title(title)
                    .content(content)
                    .status(status)
                    .readStatus(0) // 默认未读
                    .sendTime(LocalDateTime.now())
                    .isDelete(0)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();

            boolean result = this.save(record);

            if (result) {
                log.info("通知记录保存成功: userId={}, title={}, status={}", userId, title, status);
            } else {
                log.error("通知记录保存失败: userId={}, title={}", userId, title);
            }

            return result;
        } catch (Exception e) {
            log.error("保存通知记录时发生异常: userId={}, error={}", userId, e.getMessage(), e);
        }
        return false;
    }

    @Override
    public Long createNotificationRecord(Long userId, String title, String content, Integer status) {
        try {
            NotificationRecordPo record = NotificationRecordPo.builder()
                    .userId(userId)
                    .title(title)
                    .content(content)
                    .status(status)
                    .readStatus(0) // 默认未读
                    .sendTime(null) // 待发送状态，发送时间为空
                    .isDelete(0)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();

            boolean result = this.save(record);

            if (result) {
                log.info("通知记录创建成功: recordId={}, userId={}, title={}, status={}",
                        record.getId(), userId, title, status);
                return record.getId();
            } else {
                log.error("通知记录创建失败: userId={}, title={}", userId, title);
                return null;
            }
        } catch (Exception e) {
            log.error("创建通知记录时发生异常: userId={}, error={}", userId, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean updateRecordStatus(Long recordId, Integer status) {
        try {
            NotificationRecordPo record = this.getById(recordId);
            if (record == null) {
                log.error("通知记录不存在: recordId={}", recordId);
                return false;
            }

            record.setStatus(status);
            record.setSendTime(LocalDateTime.now());
            record.setUpdateTime(LocalDateTime.now());

            boolean result = this.updateById(record);

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

            queryWrapper.eq("status", NotificationRecordStatusEnum.SUCCESS.getCode());

            if(queryDto.getQueryRole() == 1){
                 queryWrapper.eq("isDelete", 0);
            }

            if(queryDto.getUserId() != null) {
                queryWrapper .eq("userId", queryDto.getUserId());
            }

            if(queryDto.getReadStatus() != null) {
                if (queryDto.getReadStatus() == 0) {
                    // 兼容历史数据：将 readStatus 为空视为未读
                    queryWrapper.and(w -> w.eq("readStatus", 0).or().isNull("readStatus"));
                } else {
                    queryWrapper.eq("readStatus", queryDto.getReadStatus());
                }
            }

            // 根据标题关键词查询
            if (StrUtil.isNotBlank(queryDto.getTitleKeyword())) {
                queryWrapper.like("title", queryDto.getTitleKeyword());
            }

            // 根据创建时间范围查询（秒级时间戳转换为LocalDateTime）
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

            // 转换为 VO
            Page<NotificationRecordVo> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
            List<NotificationRecordVo> voList = resultPage.getRecords().stream()
                    .map(this::convertToVo)
                    .collect(Collectors.toList());
            voPage.setRecords(voList);

            log.info("查询通知记录成功: userId={}, total={}, current={}, size={}",
                    queryDto.getUserId(), voPage.getTotal(), voPage.getCurrent(), voPage.getSize());
            return voPage;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询通知记录时发生异常: queryDto={}, error={}", queryDto, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询通知记录失败");
        }
    }

    @Override
    public boolean deleteNotificationRecord(Long id) {
        try {
            NotificationRecordPo record = this.getById(id);
            if (record == null) {
                log.error("通知记录不存在: id={}", id);
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "通知记录不存在");
            }

            record.setIsDelete(1);
            record.setUpdateTime(LocalDateTime.now());
            boolean result = this.updateById(record);

            if (result) {
                log.info("删除通知记录成功: id={}", id);
            } else {
                log.error("删除通知记录失败: id={}", id);
            }

            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除通知记录时发生异常: id={}, error={}", id, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除通知记录失败");
        }
    }

    @Override
    public boolean batchDeleteNotificationRecords(List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "ID列表不能为空");
            }

            UpdateWrapper<NotificationRecordPo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("id", ids)
                    .eq("isDelete", 0)
                    .set("isDelete", 1)
                    .set("updateTime", LocalDateTime.now());

            boolean result = this.update(updateWrapper);

            if (result) {
                log.info("批量删除通知记录成功: count={}", ids.size());
            } else {
                log.error("批量删除通知记录失败: ids={}", ids);
            }

            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除通知记录时发生异常: ids={}, error={}", ids, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "批量删除通知记录失败");
        }
    }

    @Override
    public boolean clearUserNotifications(Long userId) {
        try {
            if (userId == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
            }

            UpdateWrapper<NotificationRecordPo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("userId", userId)
                    .eq("isDelete", 0)
                    .eq("readStatus", 1) // 只删除已读消息
                    .set("isDelete", 1)
                    .set("updateTime", LocalDateTime.now());

            boolean result = this.update(updateWrapper);

            if (result) {
                log.info("清空用户已读消息成功: userId={}", userId);
            } else {
                log.warn("清空用户已读消息失败或无已读消息: userId={}", userId);
            }

            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("清空用户通知记录时发生异常: userId={}, error={}", userId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "清空用户通知记录失败");
        }
    }

    @Override
    public boolean batchMarkAsRead(List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "ID列表不能为空");
            }

            UpdateWrapper<NotificationRecordPo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("id", ids)
                    .eq("isDelete", 0)
                    .set("readStatus", 1)
                    .set("updateTime", LocalDateTime.now());

            boolean result = this.update(updateWrapper);

            if (result) {
                log.info("批量标记消息为已读成功: count={}", ids.size());
            } else {
                log.error("批量标记消息为已读失败: ids={}", ids);
            }

            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量标记消息为已读时发生异常: ids={}, error={}", ids, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "批量标记消息为已读失败");
        }
    }

    @Override
    public boolean markAllAsRead(Long userId) {
        try {
            if (userId == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
            }

            UpdateWrapper<NotificationRecordPo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("userId", userId)
                    .eq("isDelete", 0)
                    .and(wrapper -> wrapper.eq("readStatus", 0).or().isNull("readStatus"))
                    .set("readStatus", 1)
                    .set("updateTime", LocalDateTime.now());

            boolean result = this.update(updateWrapper);

            if (result) {
                log.info("标记用户所有未读消息为已读成功: userId={}", userId);
            } else {
                log.warn("标记用户所有未读消息为已读失败或无未读消息: userId={}", userId);
            }

            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("标记用户所有未读消息为已读时发生异常: userId={}, error={}", userId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "标记用户所有未读消息为已读失败");
        }
    }

    /**
     * 将 Po 转换为 Vo
     */
    private NotificationRecordVo convertToVo(NotificationRecordPo po) {
        NotificationRecordStatusEnum statusEnum = NotificationRecordStatusEnum.getByCode(po.getStatus());

        return NotificationRecordVo.builder()
                .id(po.getId())
                .userId(po.getUserId())
                .title(po.getTitle())
                .content(po.getContent())
                .status(po.getStatus())
                .statusDesc(statusEnum != null ? statusEnum.getDesc() : "未知")
                .readStatus(po.getReadStatus())
                .sendTime(po.getSendTime())
                .createTime(po.getCreateTime())
                .build();
    }
}

