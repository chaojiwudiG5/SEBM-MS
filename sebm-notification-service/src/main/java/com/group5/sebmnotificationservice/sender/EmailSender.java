package com.group5.sebmnotificationservice.sender;

import com.group5.sebmserviceclient.service.UserFeignClient;
import com.group5.sebmmodels.dto.user.UserDto;
import com.group5.sebmnotificationservice.enums.NotificationMethodEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 邮件发送器实现类
 * 实现邮件渠道的消息发送
 */
@Slf4j
@Component
public class EmailSender extends ChannelMsgSender {

    
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserFeignClient userService;
    
    @Override
    public NotificationMethodEnum getChannelType() {
        return NotificationMethodEnum.EMAIL;
    }


    @Override
    public boolean sendNotification(Long userId, String subject, String content) {
        try {
            log.info("开始发送邮件通知 - 用户ID: {}, 主题: {}", userId, subject);
            
            // 验证用户ID
            if (userId == null) {
                log.error("用户ID不能为空");
                return false;
            }

            // 获取用户邮箱地址
            String email = getUserContactInfo(userId);
            if (email == null) {
                log.error("用户邮箱地址为空: userId={}", userId);
                return false;
            }

            if(!isValidEmail(email)) {
                log.error("用户邮箱格式错误: userId={}, email:{}", userId, email);
                return false;
            }
            
            // 创建邮件消息
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(subject != null ? subject : "系统通知");
            message.setText(content);
            
            // 发送邮件
            mailSender.send(message);
            
            log.info("邮件发送成功 - 用户ID: {}, 邮箱: {}, 主题: {}", userId, email, subject);
            return true;
            
        } catch (Exception e) {
            log.error("邮件发送失败 - 用户ID: {}, 错误信息: {}", userId, e.getMessage(), e);
        }
        return false;
    }

    /**
     * 获取用户联系方式
     * @param userId
     * @return
     */
    public String getUserContactInfo(Long userId) {
        UserDto userDto = userService.getCurrentUserDtoFromID(userId);
        if(Objects.nonNull(userDto)){
            return userDto.getEmail();
        }
        return null;
    }


    /**
     * 验证邮箱格式
     * @param email 邮箱地址
     * @return 是否有效
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // 简单的邮箱格式验证
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
}
