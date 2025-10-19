package com.group5.sebmcommon.constant;

public class NotificationConstant {
    /**
     * 模板标题最大长度
     */
    public static final int MAX_TEMPLATE_TITLE_LENGTH = 100;

    /**
     * 模板内容最大长度
     */
    public static final int MAX_TEMPLATE_CONTENT_LENGTH = 1000;

    /**
     * 时间偏移量最小值
     */
    public static final int MIN_TIME_OFFSET = 0;

    /**
     * 时间偏移量最大值（7天的秒数：7*24*60*60）
     */
    public static final int MAX_TIME_OFFSET_SECONDS = 604800;

    // Business status constants
    public static final Integer TEMPLATE_STATUS_ACTIVE = 1;
    public static final Integer TEMPLATE_STATUS_DISABLED = 0;

    public static final Integer NOT_DELETED = 0;

    /**
     * 通知消息主题
     */
    public static final String NOTIFICATION_TOPIC = "notification-topic";
    /**
     * 即时通知标签
     */
    public static final String IMMEDIATE_TAG = "immediate";

    /**
     * 延迟通知标签
     */
    public static final String DELAY_TAG = "delay";

    // ===== 限流相关常量 =====
    /**
     * 每个用户每分钟最多发送通知数
     */
    public static final int MAX_NOTIFICATIONS_PER_MINUTE = 10;

    /**
     * 每个用户每小时最多发送通知数
     */
    public static final int MAX_NOTIFICATIONS_PER_HOUR = 100;

    /**
     * 每个用户每天最多发送通知数
     */
    public static final int MAX_NOTIFICATIONS_PER_DAY = 500;
}
