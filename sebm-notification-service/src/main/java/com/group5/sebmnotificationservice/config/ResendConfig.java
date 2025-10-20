package com.group5.sebmnotificationservice.config;

import com.resend.Resend;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Resend 邮件服务配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "resend")
public class ResendConfig {

    /**
     * Resend API 密钥
     */
    private String apiKey;

    /**
     * 发件人邮箱
     */
    private String fromEmail;

    /**
     * 发件人名称
     */
    private String fromName;

    /**
     * 创建 Resend 客户端
     */
    @Bean
    public Resend resend() {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("Resend API key is not configured. Please set RESEND_API_KEY environment variable.");
        }
        return new Resend(apiKey);
    }
}


