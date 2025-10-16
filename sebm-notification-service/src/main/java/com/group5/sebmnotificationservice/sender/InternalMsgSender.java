package com.group5.sebmnotificationservice.sender;

import com.group5.sebmserviceclient.service.UserFeignClient;
import com.group5.sebmmodels.dto.user.UserDto;
import com.group5.sebmnotificationservice.enums.NotificationMethodEnum;
import com.group5.sebmnotificationservice.websocket.NotificationWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内部消息发送器实现类
 * 实现系统内部消息通知
 */
@Slf4j
@Component
public class InternalMsgSender extends ChannelMsgSender {

    @Autowired
    private UserFeignClient userService;

    @Autowired
    private NotificationWebSocketHandler webSocketHandler;

    // 简单的内存存储，实际项目中应该使用数据库或消息队列
    private final Map<String, StringBuilder> userMessages = new ConcurrentHashMap<>();

    @Override
    public NotificationMethodEnum getChannelType() {
        return NotificationMethodEnum.INTERNAL_MSG;
    }

    @Override
    public boolean sendNotification(Long userId, String subject, String content) {
        try {
            log.info("开始发送内部消息通知 - 用户ID: {}, 主题: {}", userId, subject);

            // 验证用户ID
            if (userId == null) {
                log.error("用户ID不能为空");
                return false;
            }

            // 验证用户是否存在
            if (!isUserExists(userId)) {
                log.warn("用户不存在: userId={}", userId);
                return false;
            }

            // 获取用户信息（用于日志记录）
            UserDto userDto = userService.getCurrentUserDtoFromID(userId);
            String username = Objects.nonNull(userDto) ? userDto.getUsername() : "用户" + userId;

            // 格式化消息
            String formattedMessage = formatInternalMessage(subject, content, null);

            // 存储消息到用户消息队列中
            userMessages.computeIfAbsent(userId.toString(), k -> new StringBuilder())
                      .append(formattedMessage)
                      .append("\n");

            // 通过WebSocket实时推送消息
            boolean webSocketSuccess = pushToUserViaWebSocket(userId.toString(), subject, content);
            
            // 如果WebSocket推送失败，记录日志但不影响整体发送结果
            if (!webSocketSuccess) {
                log.warn("WebSocket推送失败，但消息已存储到用户消息队列 - 用户ID: {}", userId);
            }

            log.info("内部消息发送成功 - 用户ID: {}, 用户名: {}, 主题: {}, WebSocket推送: {}", 
                    userId, username, subject, webSocketSuccess ? "成功" : "失败");
            return true;

        } catch (Exception e) {
            log.error("内部消息发送失败 - 用户ID: {}, 错误信息: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 格式化内部消息
     * @param subject 主题
     * @param content 内容
     * @param templateId 模板ID
     * @return 格式化后的消息
     */
    private String formatInternalMessage(String subject, String content, Long templateId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return String.format("[%s] %s: %s %s",
                timestamp,
                subject,
                content,
                templateId != null ? "(模板ID: " + templateId + ")" : "");
    }

    /**
     * 通过WebSocket推送消息给用户
     * @param userId 用户ID
     * @param subject 消息主题
     * @param content 消息内容
     * @return 是否推送成功
     */
    private boolean pushToUserViaWebSocket(String userId, String subject, String content) {
        try {
            // 检查用户是否在线
            if (!webSocketHandler.isUserOnline(userId)) {
                log.info("用户 {} 不在线，跳过WebSocket推送", userId);
                return false;
            }

            // 通过WebSocket发送通知消息
            boolean success = webSocketHandler.sendNotificationToUser(userId, subject, content, "internal");
            
            if (success) {
                log.info("WebSocket推送成功 - 用户ID: {}, 主题: {}", userId, subject);
            } else {
                log.warn("WebSocket推送失败 - 用户ID: {}", userId);
            }
            
            return success;
            
        } catch (Exception e) {
            log.error("WebSocket推送异常 - 用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 广播消息给所有在线用户
     * @param subject 消息主题
     * @param content 消息内容
     * @return 推送成功的用户数量
     */
    public int broadcastMessage(String subject, String content) {
        try {
            String message = formatInternalMessage(subject, content, null);
            int successCount = webSocketHandler.broadcastMessage(message);
            log.info("广播消息完成 - 主题: {}, 成功推送给 {} 个用户", subject, successCount);
            return successCount;
        } catch (Exception e) {
            log.error("广播消息失败 - 主题: {}, 错误: {}", subject, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 获取在线用户数量
     * @return 在线用户数量
     */
    public int getOnlineUserCount() {
        return webSocketHandler.getOnlineUserCount();
    }

    /**
     * 检查用户是否在线
     * @param userId 用户ID
     * @return 是否在线
     */
    public boolean isUserOnline(String userId) {
        return webSocketHandler.isUserOnline(userId);
    }

    /**
     * 获取用户的所有消息
     * @param userId 用户ID
     * @return 用户消息列表
     */
    public String getUserMessages(String userId) {
        StringBuilder messages = userMessages.get(userId);
        return messages != null ? messages.toString() : "暂无消息";
    }

    /**
     * 清空用户消息
     * @param userId 用户ID
     */
    public void clearUserMessages(String userId) {
        userMessages.remove(userId);
        log.info("已清空用户 {} 的消息记录", userId);
    }

    /**
     * 检查用户是否存在
     * @param userId 用户ID
     * @return 用户是否存在
     */
    private boolean isUserExists(Long userId) {
        try {
            UserDto userDto = userService.getCurrentUserDtoFromID(userId);
            return Objects.nonNull(userDto);
        } catch (Exception e) {
            log.error("检查用户存在性失败 - userId: {}, error: {}", userId, e.getMessage());
        }
        return false;
    }

    /**
     * 获取消息统计
     * @return 消息统计信息
     */
    public Map<String, Object> getMessageStats() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("totalUsers", userMessages.size());
        stats.put("totalMessages", userMessages.values().stream()
                .mapToInt(sb -> sb.toString().split("\n").length)
                .sum());
        stats.put("onlineUsers", webSocketHandler.getOnlineUserCount());
        stats.put("onlineUserList", webSocketHandler.getOnlineUsers());
        stats.put("timestamp", LocalDateTime.now());
        return stats;
    }
}
