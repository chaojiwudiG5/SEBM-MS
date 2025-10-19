package com.group5.sebmnotificationservice.config;

import com.group5.sebmnotificationservice.websocket.LoggingHandshakeInterceptor;
import com.group5.sebmnotificationservice.websocket.NotificationWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket配置类
 * 配置WebSocket端点和处理器
 */
@Configuration
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private NotificationWebSocketHandler notificationWebSocketHandler;

    @Autowired
    private LoggingHandshakeInterceptor loggingHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 1) 原生 WebSocket 端点（推荐前端使用原生 WebSocket 直接连接该端点）
        registry.addHandler(notificationWebSocketHandler, "/ws/notification")
                .addInterceptors(loggingHandshakeInterceptor)
                .setAllowedOrigins("*");

        // 2) SockJS 端点（如需兼容旧环境，可使用 SockJS 客户端连接）
        registry.addHandler(notificationWebSocketHandler, "/ws/notification/sockjs")
                .addInterceptors(loggingHandshakeInterceptor)
                .setAllowedOrigins("*")
                .withSockJS();

        log.info("WebSocket 已注册: native=/ws/notification, sockjs=/ws/notification/sockjs, cors=*, interceptors=[LoggingHandshakeInterceptor]");
    }
}
