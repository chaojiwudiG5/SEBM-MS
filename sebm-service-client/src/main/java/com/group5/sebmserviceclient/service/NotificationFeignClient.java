package com.group5.sebmserviceclient.service;

import com.group5.sebmmodels.dto.notification.SendNotificationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 通知服务Feign客户端
 * 用于服务间调用通知相关接口
 */
@FeignClient(name = "sebm-notification-service", path = "/api/notification/inner")
public interface NotificationFeignClient {

    /**
     * 发送通知
     * @param sendNotificationDto 发送通知请求DTO
     * @return 是否发送成功
     */
    @PostMapping("/sendNotification")
    Boolean sendNotification(@RequestBody SendNotificationDto sendNotificationDto);

    /**
     * 创建通知记录
     * @param userId 用户ID
     * @param title 通知标题
     * @param content 通知内容
     * @param status 发送状态
     * @return 记录ID，失败返回null
     */
    @PostMapping("/createRecord")
    Long createNotificationRecord(@RequestParam("userId") Long userId,
                                   @RequestParam("title") String title,
                                   @RequestParam("content") String content,
                                   @RequestParam("status") Integer status);

    /**
     * 更新通知记录状态
     * @param recordId 记录ID
     * @param status 发送状态
     * @return 是否更新成功
     */
    @PostMapping("/updateRecordStatus")
    Boolean updateRecordStatus(@RequestParam("recordId") Long recordId,
                               @RequestParam("status") Integer status);

    /**
     * 获取用户未读通知数量
     * @param userId 用户ID
     * @return 未读通知数量
     */
    @GetMapping("/unreadCount")
    Long getUnreadCount(@RequestParam("userId") Long userId);
}

