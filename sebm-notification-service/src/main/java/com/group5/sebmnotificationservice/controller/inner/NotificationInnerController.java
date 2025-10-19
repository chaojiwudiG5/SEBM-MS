package com.group5.sebmnotificationservice.controller.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.group5.sebmmodels.dto.notification.SendNotificationDto;
import com.group5.sebmmodels.entity.NotificationRecordPo;
import com.group5.sebmnotificationservice.service.NotificationRecordService;
import com.group5.sebmnotificationservice.service.NotificationService;
import com.group5.sebmserviceclient.service.NotificationFeignClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通知服务内部接口控制器
 * 提供给其他微服务调用的接口
 */
@Slf4j
@RestController
@RequestMapping("/inner")
public class NotificationInnerController implements NotificationFeignClient {

    @Resource
    private NotificationService notificationService;

    @Resource
    private NotificationRecordService notificationRecordService;

    @Override
    public Boolean sendNotification(SendNotificationDto sendNotificationDto) {
        try {
            log.info("内部调用：发送通知 - {}", sendNotificationDto);
            return notificationService.sendNotification(sendNotificationDto);
        } catch (Exception e) {
            log.error("内部调用：发送通知失败 - {}", sendNotificationDto, e);
            return false;
        }
    }

    @Override
    public Long createNotificationRecord(Long userId, String title, String content, Integer status) {
        try {
            log.info("内部调用：创建通知记录 - userId={}, title={}, status={}", userId, title, status);
            return notificationRecordService.createNotificationRecord(userId, title, content, status);
        } catch (Exception e) {
            log.error("内部调用：创建通知记录失败 - userId={}, title={}", userId, title, e);
            return null;
        }
    }

    @Override
    public Boolean updateRecordStatus(Long recordId, Integer status) {
        try {
            log.info("内部调用：更新通知记录状态 - recordId={}, status={}", recordId, status);
            return notificationRecordService.updateRecordStatus(recordId, status);
        } catch (Exception e) {
            log.error("内部调用：更新通知记录状态失败 - recordId={}, status={}", recordId, status, e);
            return false;
        }
    }

    @Override
    public Long getUnreadCount(Long userId) {
        try {
            log.info("内部调用：获取用户未读通知数量 - userId={}", userId);
            QueryWrapper<NotificationRecordPo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId", userId)
                    .eq("status", 1)
                    .eq("readStatus", 0)
                    .eq("isDelete", 0);

            long count = notificationRecordService.count(queryWrapper);
            log.info("内部调用：用户未读通知数量 - userId={}, count={}", userId, count);
            return count;
        } catch (Exception e) {
            log.error("内部调用：获取用户未读通知数量失败 - userId={}", userId, e);
            return 0L;
        }
    }
}

