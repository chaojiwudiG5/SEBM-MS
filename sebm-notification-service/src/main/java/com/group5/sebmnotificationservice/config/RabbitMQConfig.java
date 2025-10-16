package com.group5.sebmnotificationservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.group5.sebmcommon.constant.NotificationConstant.*;

/**
 * RabbitMQ配置类 - 使用延迟消息插件实现高精度延迟
 */
@Slf4j
@Configuration
public class RabbitMQConfig {

    /**
     * 消息转换器
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate配置
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        
        // 设置发布确认
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("消息发送成功: {}", correlationData);
            } else {
                log.error("消息发送失败: {}, 原因: {}", correlationData, cause);
            }
        });
        
        // 设置返回回调
        template.setReturnsCallback(returned -> {
            log.error("消息返回: {}, 回复码: {}, 回复文本: {}", 
                    returned.getMessage(), returned.getReplyCode(), returned.getReplyText());
        });
        
        return template;
    }

    /**
     * 监听器容器工厂
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

    // ==================== 即时消息配置 ====================
    
    /**
     * 通知主题交换器
     */
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_TOPIC, true, false);
    }

    /**
     * 即时通知队列
     */
    @Bean
    public Queue immediateNotificationQueue() {
        return QueueBuilder.durable("notification.immediate.queue").build();
    }

    /**
     * 即时通知队列绑定
     */
    @Bean
    public Binding immediateNotificationBinding() {
        return BindingBuilder
                .bind(immediateNotificationQueue())
                .to(notificationExchange())
                .with("notification.immediate");
    }

    // ==================== 延迟消息配置（使用延迟消息插件）====================
    
    /**
     * 延迟消息交换器 - 使用延迟消息插件
     * 注意：这里使用CustomExchange而不是TopicExchange
     */
    @Bean
    public CustomExchange delayExchange() {
        return new CustomExchange(
                "notification.delay.exchange", 
                "x-delayed-message",  // 延迟消息插件的交换器类型
                true, 
                false,
                java.util.Map.of("x-delayed-type", "topic")  // 延迟消息转发到topic类型
        );
    }

    /**
     * 延迟消息队列
     */
    @Bean
    public Queue delayNotificationQueue() {
        return QueueBuilder.durable("notification.delay.queue").build();
    }

    /**
     * 延迟消息队列绑定
     */
    @Bean
    public Binding delayNotificationBinding() {
        return BindingBuilder
                .bind(delayNotificationQueue())
                .to(delayExchange())
                .with("notification.delay")
                .noargs();
    }

}
