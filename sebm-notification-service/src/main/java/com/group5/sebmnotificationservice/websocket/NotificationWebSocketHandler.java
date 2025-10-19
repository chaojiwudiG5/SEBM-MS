package com.group5.sebmnotificationservice.websocket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通知WebSocket处理器
 * 处理WebSocket连接和消息推送
 */
@Slf4j
@Component
public class NotificationWebSocketHandler implements WebSocketHandler {

    // 存储用户ID和WebSocket会话的映射关系
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    
    // JSON解析器
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.put(userId, session);
            log.info("用户 {} 建立WebSocket连接成功, sessionId={}, remote={}, uri={}",
                    userId,
                    session.getId(),
                    session.getRemoteAddress(),
                    session.getUri());
            // 按需发送系统消息：已移除默认的“连接成功”下行推送，避免前端收到冗余提示
        } else {
            log.warn("无法获取用户ID，关闭连接");
            session.close();
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String userId = getUserIdFromSession(session);
        log.info("收到用户 {} 的消息: {}", userId, message.getPayload());
        
        // 处理客户端发送的消息（如心跳、状态更新等）
        if (message instanceof TextMessage) {
            String payload = ((TextMessage) message).getPayload();
            handleClientMessage(session, payload);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String userId = getUserIdFromSession(session);
        log.error("用户 {} WebSocket传输错误: {}, sessionId={}, remote={}",
                userId, exception.getMessage(), session.getId(), session.getRemoteAddress(), exception);
        
        // 移除失效的会话
        if (userId != null) {
            userSessions.remove(userId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.remove(userId);
            log.info("用户 {} WebSocket连接已关闭: {}, sessionId={}, remote={}",
                    userId, closeStatus, session.getId(), session.getRemoteAddress());
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 向指定用户发送消息
     * @param userId 用户ID
     * @param message 消息内容
     * @return 是否发送成功
     */
    public boolean sendMessageToUser(String userId, String message) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                sendMessage(session, message);
                log.info("成功向用户 {} 发送WebSocket消息", userId);
                return true;
            } catch (Exception e) {
                log.error("向用户 {} 发送WebSocket消息失败: {}", userId, e.getMessage(), e);
                // 移除失效的会话
                userSessions.remove(userId);
                return false;
            }
        } else {
            log.warn("用户 {} 的WebSocket会话不存在或已关闭", userId);
            return false;
        }
    }

    /**
     * 向指定用户发送通知消息
     * @param userId 用户ID
     * @param subject 消息主题
     * @param content 消息内容
     * @param notificationType 通知类型
     * @return 是否发送成功
     */
    public boolean sendNotificationToUser(String userId, String subject, String content, String notificationType) {
        try {
            Map<String, Object> notification = createNotificationMessage(subject, content, notificationType);
            String message = JSON.toJSONString(notification);
            return sendMessageToUser(userId, message);
        } catch (Exception e) {
            log.error("发送通知消息失败 - userId: {}, error: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 广播消息给所有在线用户
     * @param message 消息内容
     * @return 发送成功的用户数量
     */
    public int broadcastMessage(String message) {
        int successCount = 0;
        for (Map.Entry<String, WebSocketSession> entry : userSessions.entrySet()) {
            try {
                sendMessage(entry.getValue(), message);
                successCount++;
            } catch (Exception e) {
                log.error("广播消息给用户 {} 失败: {}", entry.getKey(), e.getMessage());
            }
        }
        log.info("广播消息完成，成功发送给 {} 个用户", successCount);
        return successCount;
    }

    /**
     * 获取在线用户数量
     * @return 在线用户数量
     */
    public int getOnlineUserCount() {
        return userSessions.size();
    }

    /**
     * 获取在线用户列表
     * @return 在线用户ID列表
     */
    public String[] getOnlineUsers() {
        return userSessions.keySet().toArray(new String[0]);
    }

    /**
     * 检查用户是否在线
     * @param userId 用户ID
     * @return 是否在线
     */
    public boolean isUserOnline(String userId) {
        WebSocketSession session = userSessions.get(userId);
        return session != null && session.isOpen();
    }

    /**
     * 从WebSocket会话中获取用户ID
     * @param session WebSocket会话
     * @return 用户ID
     */
    private String getUserIdFromSession(WebSocketSession session) {
        // 从查询参数中获取用户ID
        String query = session.getUri().getQuery();
        if (query != null && query.contains("userId=")) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("userId=")) {
                    return param.substring(7);
                }
            }
        }
        return null;
    }

    /**
     * 发送消息到WebSocket会话
     * @param session WebSocket会话
     * @param message 消息内容
     * @throws IOException IO异常
     */
    private void sendMessage(WebSocketSession session, String message) throws IOException {
        if (session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        }
    }

    /**
     * 创建系统消息
     * @param subject 主题
     * @param content 内容
     * @return 系统消息JSON字符串
     */
    private String createSystemMessage(String subject, String content) {
        Map<String, Object> message = Map.of(
                "type", "system",
                "subject", subject,
                "content", content,
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
        return JSON.toJSONString(message);
    }

    /**
     * 创建通知消息
     * @param subject 主题
     * @param content 内容
     * @param notificationType 通知类型
     * @return 通知消息Map
     */
    private Map<String, Object> createNotificationMessage(String subject, String content, String notificationType) {
        Map<String, Object> notification = new ConcurrentHashMap<>();
        notification.put("type", "notification");
        notification.put("subject", subject);
        notification.put("content", content);
        notification.put("notificationType", notificationType);
        notification.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return notification;
    }

    /**
     * 处理客户端发送的消息
     * @param session WebSocket会话
     * @param payload 消息内容
     */
    private void handleClientMessage(WebSocketSession session, String payload) {
        try {
            // 解析客户端消息
            Map<String, Object> message = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
            String type = (String) message.get("type");
            
            switch (type) {
                case "ping":
                    // 处理心跳消息
                    sendMessage(session, JSON.toJSONString(Map.of("type", "pong", "timestamp", System.currentTimeMillis())));
                    break;
                case "status":
                    // 处理状态更新消息
                    log.info("用户状态更新: {}", message);
                    break;
                default:
                    log.warn("未知的消息类型: {}", type);
            }
        } catch (Exception e) {
            log.error("处理客户端消息失败: {}", e.getMessage(), e);
        }
    }
}
