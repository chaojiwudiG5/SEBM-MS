package com.group5.sebmnotificationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * 通知限流服务 - 基于Redis滑动窗口算法
 */
@Slf4j
@Service
public class NotificationRateLimiter {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 限流配置常量
    private static final String RATE_LIMIT_KEY_PREFIX = "rate_limit:notification:";
    
    // 每个用户每分钟最多发送通知数
    private static final int MAX_REQUESTS_PER_MINUTE = 10;
    
    // 每个用户每小时最多发送通知数
    private static final int MAX_REQUESTS_PER_HOUR = 100;
    
    // 每个用户每天最多发送通知数
    private static final int MAX_REQUESTS_PER_DAY = 500;
    
    // 窗口时间（秒）
    private static final long WINDOW_SIZE_MINUTE = 60; // 1分钟
    private static final long WINDOW_SIZE_HOUR = 3600; // 1小时
    private static final long WINDOW_SIZE_DAY = 86400; // 1天

    /**
     * 检查是否允许发送通知（多级限流：分钟、小时、天）
     * 
     * @param userId 用户ID
     * @return true-允许发送，false-触发限流
     */
    public boolean allowNotification(Long userId) {
        if (userId == null) {
            log.warn("用户ID为空，拒绝发送通知");
            return false;
        }

        long currentTime = System.currentTimeMillis();
        
        // 多级限流检查
        boolean allowedMinute = checkRateLimit(userId, currentTime, WINDOW_SIZE_MINUTE, MAX_REQUESTS_PER_MINUTE, "分钟");
        if (!allowedMinute) {
            return false;
        }
        
        boolean allowedHour = checkRateLimit(userId, currentTime, WINDOW_SIZE_HOUR, MAX_REQUESTS_PER_HOUR, "小时");
        if (!allowedHour) {
            return false;
        }

        return checkRateLimit(userId, currentTime, WINDOW_SIZE_DAY, MAX_REQUESTS_PER_DAY, "天");
    }

    /**
     * 检查单个窗口的限流
     * 
     * @param userId 用户ID
     * @param currentTime 当前时间戳（毫秒）
     * @param windowSize 窗口大小（秒）
     * @param maxRequests 窗口内最大请求数
     * @param windowName 窗口名称（用于日志）
     * @return true-允许，false-拒绝
     */
    private boolean checkRateLimit(Long userId, long currentTime, long windowSize, int maxRequests, String windowName) {
        String key = buildRateLimitKey(userId, windowSize);
        
        try {
            // 1. 移除窗口外的旧数据
            long windowStart = currentTime - (windowSize * 1000);
            redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);
            
            // 2. 获取当前窗口内的请求数
            Long count = redisTemplate.opsForZSet().zCard(key);
            if (count == null) {
                count = 0L;
            }
            
            // 3. 判断是否超过限制
            if (count >= maxRequests) {
                log.warn("用户 {} 触发{}级限流: 当前请求数={}, 限制={}", userId, windowName, count, maxRequests);
                return false;
            }
            
            // 4. 添加当前请求到滑动窗口
            String requestId = UUID.randomUUID().toString();
            redisTemplate.opsForZSet().add(key, requestId, currentTime);
            
            // 5. 设置key的过期时间（窗口大小的2倍，确保数据能被清理）
            redisTemplate.expire(key, Duration.ofSeconds(windowSize * 2));
            
            log.debug("用户 {} {}级限流检查通过: 当前请求数={}/{}", userId, windowName, count + 1, maxRequests);
            return true;
            
        } catch (Exception e) {
            log.error("限流检查异常: userId={}, windowName={}, error={}", userId, windowName, e.getMessage(), e);
            // 异常情况下允许通过，避免影响业务
            return true;
        }
    }

    /**
     * 构建限流Redis Key
     * 
     * @param userId 用户ID
     * @param windowSize 窗口大小（秒）
     * @return Redis Key
     */
    private String buildRateLimitKey(Long userId, long windowSize) {
        return RATE_LIMIT_KEY_PREFIX + userId + ":" + windowSize;
    }

    /**
     * 获取用户在指定窗口内的剩余配额
     * 
     * @param userId 用户ID
     * @param windowSize 窗口大小（秒）
     * @param maxRequests 最大请求数
     * @return 剩余配额
     */
    public long getRemainingQuota(Long userId, long windowSize, int maxRequests) {
        String key = buildRateLimitKey(userId, windowSize);
        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime - (windowSize * 1000);
        
        try {
            // 移除窗口外的旧数据
            redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);
            
            // 获取当前窗口内的请求数
            Long count = redisTemplate.opsForZSet().zCard(key);
            if (count == null) {
                count = 0L;
            }
            
            return Math.max(0, maxRequests - count);
        } catch (Exception e) {
            log.error("获取剩余配额异常: userId={}, error={}", userId, e.getMessage(), e);
            return maxRequests;
        }
    }

    /**
     * 获取用户每分钟的剩余配额
     */
    public long getRemainingQuotaPerMinute(Long userId) {
        return getRemainingQuota(userId, WINDOW_SIZE_MINUTE, MAX_REQUESTS_PER_MINUTE);
    }

    /**
     * 获取用户每小时的剩余配额
     */
    public long getRemainingQuotaPerHour(Long userId) {
        return getRemainingQuota(userId, WINDOW_SIZE_HOUR, MAX_REQUESTS_PER_HOUR);
    }

    /**
     * 获取用户每天的剩余配额
     */
    public long getRemainingQuotaPerDay(Long userId) {
        return getRemainingQuota(userId, WINDOW_SIZE_DAY, MAX_REQUESTS_PER_DAY);
    }

    /**
     * 清除用户的限流记录（管理员功能）
     * 
     * @param userId 用户ID
     */
    public void clearRateLimit(Long userId) {
        try {
            String keyPattern = RATE_LIMIT_KEY_PREFIX + userId + ":*";
            redisTemplate.delete(redisTemplate.keys(keyPattern));
            log.info("已清除用户 {} 的限流记录", userId);
        } catch (Exception e) {
            log.error("清除限流记录异常: userId={}, error={}", userId, e.getMessage(), e);
        }
    }
}

