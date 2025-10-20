package com.group5.sebmnotificationservice.sender;

import com.group5.sebmserviceclient.service.UserFeignClient;
import com.group5.sebmmodels.dto.user.UserDto;
import com.group5.sebmnotificationservice.config.ResendConfig;
import com.group5.sebmnotificationservice.enums.NotificationMethodEnum;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 邮件发送器实现类
 * 使用 Resend API 实现邮件渠道的消息发送
 */
@Slf4j
@Component
public class EmailSender extends ChannelMsgSender {

    @Autowired
    private Resend resend;

    @Autowired
    private ResendConfig resendConfig;

    @Autowired
    private UserFeignClient userService;
    
    @Override
    public NotificationMethodEnum getChannelType() {
        return NotificationMethodEnum.EMAIL;
    }


    @Override
    public boolean sendNotification(Long userId, String subject, String content) {
        try {
            log.info("开始使用Resend发送邮件通知 - 用户ID: {}, 主题: {}", userId, subject);
            
            // 验证用户ID
            if (userId == null) {
                log.error("用户ID不能为空");
                return false;
            }

            // 获取用户邮箱地址
            String userEmail = getUserContactInfo(userId);
            if (userEmail == null) {
                log.error("用户邮箱地址为空: userId={}", userId);
                return false;
            }

            if(!isValidEmail(userEmail)) {
                log.error("用户邮箱格式错误: userId={}, email:{}", userId, userEmail);
                return false;
            }
            
            // 创建邮件主题
            String emailSubject = subject != null ? subject : "系统通知";
            
            // 构建邮件发送参数
            CreateEmailOptions emailOptions = CreateEmailOptions.builder()
                    .from(resendConfig.getFromName() + " <" + resendConfig.getFromEmail() + ">")
                    .to(userEmail)
                    .subject(emailSubject)
                    .text(content)  // 纯文本内容
                    .build();
            
            // 发送邮件
            CreateEmailResponse response = resend.emails().send(emailOptions);
            
            // 检查响应
            if (response != null && response.getId() != null) {
                log.info("Resend邮件发送成功 - 用户ID: {}, 邮箱: {}, 主题: {}, 邮件ID: {}", 
                        userId, userEmail, emailSubject, response.getId());
                return true;
            } else {
                log.error("Resend邮件发送失败 - 用户ID: {}, 邮箱: {}, 响应为空", 
                        userId, userEmail);
                return false;
            }
            
        } catch (ResendException e) {
            log.error("Resend邮件发送异常 - 用户ID: {}, 错误信息: {}", 
                    userId, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Resend邮件发送失败 - 用户ID: {}, 错误信息: {}", userId, e.getMessage(), e);
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
