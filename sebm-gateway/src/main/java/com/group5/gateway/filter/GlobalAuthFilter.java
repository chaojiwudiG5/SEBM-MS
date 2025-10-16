package com.group5.gateway.filter;

import com.group5.gateway.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import java.nio.charset.StandardCharsets;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * ✅ 全局网关过滤器：拦截请求并校验 JWT。
 * 功能：
 * 1. 禁止访问 /inner/** 路径；
 * 2. 登录注册等白名单直接放行；
 * 3. 校验 JWT 的有效性；
 * 4. 将 userId 和 role 透传给下游服务。
 */
@Component
public class GlobalAuthFilter implements GlobalFilter {

  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  // 白名单接口（不需要 JWT）
  private static final String[] WHITE_LIST = {
      "/api/user/login",
      "/api/user/register",
      "/swagger-ui",
      "/v3/api-docs",
      "/api/borrow/v3/api-docs",
      "/api/user/v3/api-docs",
      "/api/device/v3/api-docs",
      "/api/maintenance/v3/api-docs"
  };

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    String path = request.getURI().getPath();
    // 🚫 禁止访问内部接口
    if (antPathMatcher.match("**/inner/**", path)) {
      return forbidden(exchange, "Forbidden: Inner API Access Denied");
    }

    // ✅ 白名单直接放行
    for (String url : WHITE_LIST) {
      if (antPathMatcher.match(url + "**", path)) {
        return chain.filter(exchange);
      }
    }

    // 🔍 获取 Authorization 头
    String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return unauthorized(exchange, "Missing or Invalid Authorization Header");
    }

    String token = authHeader.substring(7);

    // 🔍 校验 token 是否有效
    Claims claims;
    try {
      claims = JwtUtils.parseToken(token);
    } catch (Exception e) {
      return unauthorized(exchange, "Invalid or Expired Token");
    }

    // 检查是否过期
    if (JwtUtils.isTokenExpired(token)) {
      return unauthorized(exchange, "Token Expired");
    }

    // ✅ 提取用户信息
    Long userId = JwtUtils.getUserIdFromToken(token);
    Integer role = JwtUtils.getRoleFromToken(token);

    // ✅ 将用户信息透传给下游服务（例如 userId / role）
    ServerHttpRequest mutatedRequest = request.mutate()
        .header("userId", String.valueOf(userId))
        .header("role", String.valueOf(role))
        .build();

    ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
    return chain.filter(mutatedExchange);
  }

  // --- 辅助方法 ---

  private Mono<Void> forbidden(ServerWebExchange exchange, String message) {
    return writeResponse(exchange, HttpStatus.FORBIDDEN, message);
  }

  private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
    return writeResponse(exchange, HttpStatus.UNAUTHORIZED, message);
  }

  private Mono<Void> writeResponse(ServerWebExchange exchange, HttpStatus status, String message) {
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(status);
    response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

    DataBufferFactory bufferFactory = response.bufferFactory();
    String body = String.format("{\"code\":%d,\"msg\":\"%s\"}", status.value(), message);
    DataBuffer buffer = bufferFactory.wrap(body.getBytes(StandardCharsets.UTF_8));

    return response.writeWith(Mono.just(buffer));
  }
}
