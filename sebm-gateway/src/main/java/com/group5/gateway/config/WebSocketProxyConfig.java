package com.group5.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * WebSocket 代理配置
 * 为 Gateway 添加 WebSocket 支持
 */
@Configuration
public class WebSocketProxyConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // WebSocket 路由 - 原生 WebSocket
                .route("websocket-notification", r -> r
                        .path("/api/notification/ws/**")
                        .uri("lb:ws://sebm-notification-service"))
                .build();
    }
}
