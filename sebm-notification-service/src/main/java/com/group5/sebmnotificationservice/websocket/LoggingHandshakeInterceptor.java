package com.group5.sebmnotificationservice.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * WebSocket 握手日志拦截器
 * 打印客户端关键信息，辅助排查连接问题
 */
@Slf4j
@Component
public class LoggingHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        try {
            HttpHeaders headers = request.getHeaders();
            String uri = request.getURI().toString();
            String query = request.getURI().getQuery();
            String userAgent = headers.getFirst("User-Agent");
            String origin = headers.getOrigin();

            String clientIp = null;
            if (request instanceof ServletServerHttpRequest) {
                HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
                clientIp = getClientIp(servletRequest);
            }

            // 尝试从查询参数中提取 userId 以便日志跟踪
            String userId = null;
            if (query != null && query.contains("userId=")) {
                for (String param : query.split("&")) {
                    if (param.startsWith("userId=")) {
                        userId = param.substring(7);
                        break;
                    }
                }
            }

            attributes.put("clientIp", clientIp);
            attributes.put("userAgent", userAgent);
            attributes.put("origin", origin);
            attributes.put("requestUri", uri);
            if (userId != null) {
                attributes.put("userId", userId);
            }

            log.info("WebSocket 握手开始: uri={}, userId={}, clientIp={}, origin={}", uri, userId, clientIp, origin);
            if (log.isDebugEnabled()) {
                log.debug("WebSocket 请求头: {}", headers);
            }
        } catch (Exception e) {
            log.warn("记录 WebSocket 握手信息失败: {}", e.getMessage(), e);
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("WebSocket 握手失败: uri={}, error={}", request.getURI(), exception.getMessage(), exception);
        } else {
            log.info("WebSocket 握手完成: uri={}", request.getURI());
        }
    }

    private String getClientIp(HttpServletRequest request) {
        // 优先读取代理后的真实 IP
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(',');
            return index != -1 ? ip.substring(0, index).trim() : ip.trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }
        return request.getRemoteAddr();
    }
}


