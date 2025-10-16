package com.group5.sebmnotificationservice.enums;

import lombok.Getter;

/**
 * 通知记录状态枚举
 */
@Getter
public enum NotificationRecordStatusEnum {
    
    /**
     * 待发送
     */
    PENDING(0, "待发送"),
    
    /**
     * 发送成功
     */
    SUCCESS(1, "发送成功"),
    
    /**
     * 发送失败
     */
    FAILED(2, "发送失败");
    
    private final Integer code;
    private final String desc;
    
    NotificationRecordStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据状态码获取枚举
     */
    public static NotificationRecordStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (NotificationRecordStatusEnum status : NotificationRecordStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}

