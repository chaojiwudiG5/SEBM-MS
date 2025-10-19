package com.group5.sebmcommon.enums;

import lombok.Getter;

/**
 * 通知事件类型枚举
 * 用于定义通知的时间偏移类型
 * 每个模板创建时用户自己填写event code，event类型决定时间偏移的方向
 */
@Getter
public enum NotificationTypeEnum {
    /**
     * 提前通知类型（目标时间 = 节点时间戳 - 偏移量）
     */

    ADVANCE_TYPE(-1, "提前通知"),

    /**
     * 即时通知类型（目标时间 = 节点时间戳）
     */
    IMMEDIATE_TYPE(0, "即时通知"),

    /**
     * 延迟通知类型（目标时间 = 节点时间戳 + 偏移量）
     */
    DELAY_TYPE(1, "延迟通知");

    /**
     * 事件类型代码
     */
    private final Integer typeCode;

    /**
     * 事件类型描述
     */
    private final String description;

    // 枚举必须手动编写构造函数
    NotificationTypeEnum(Integer typeCode, String description) {
        this.typeCode = typeCode;
        this.description = description;
    }

    /**
     * 根据类型代码获取枚举
     */
    public static NotificationTypeEnum parseType(Integer typeCode) {
        if (typeCode == null) {
            return IMMEDIATE_TYPE;
        }
        for (NotificationTypeEnum eventEnum : NotificationTypeEnum.values()) {
            if (eventEnum.getTypeCode().equals(typeCode)) {
                return eventEnum;
            }
        }
        return IMMEDIATE_TYPE;
    }

    /**
     * 验证是否为可用的事件类型
     */
    public static boolean isValidType(Integer typeCode) {
        return parseType(typeCode) != null;
    }
}