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

    // Business status constants
    public static final String TEMPLATE_STATUS_ACTIVE = "active";

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
}
