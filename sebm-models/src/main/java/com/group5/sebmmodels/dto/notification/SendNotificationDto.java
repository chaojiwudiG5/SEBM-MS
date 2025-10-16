package com.group5.sebmmodels.dto.notification;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送通知请求DTO - 为其他服务提供的统一接口
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendNotificationDto {

    /**
     * 通知代码枚举值
     * 使用 NotificationEventEnum
     */
    private Integer notificationEvent;
    /**
     * 接收者Id
     */
    private Long userId;

    /**
     * 占位符信息
     * 例如: {"orderNo": "12345", "equipmentName": "笔记本电脑", "remainingTime": "5分钟"}
     */
    private Map<String, Object> templateVars;

    /**
     * 节点时间戳(秒)
     */
    private Long nodeTimestamp;

}
