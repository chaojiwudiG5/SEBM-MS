package com.group5.gateway.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JWT 工具类（兼容 JDK 17+）
 */
public class JwtUtils {

    // HS256 秘钥（可存配置文件或环境变量）
    private static final String SECRET = "To say a goodbye is to die a little";

    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    // token 有效期：1小时
    private static final long EXPIRATION = 1000 * 60 * 60;

    /**
     * 生成 JWT token
     * @param userId 用户 ID
     * @return token
     */
    public static String generateToken(Long userId, Integer role) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * 解析 JWT token
     * @param token JWT 字符串
     * @return Claims
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从 token 中获取用户 ID
     * @param token JWT 字符串
     * @return 用户 ID
     */
    public static Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }
    /**
     * 从token中获取角色
     */
    public static Integer getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        return (Integer) claims.get("role");
    }

    /**
     * token 是否过期
     */
    public static boolean isTokenExpired(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().before(new Date());
    }
}
