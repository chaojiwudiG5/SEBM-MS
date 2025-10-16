package com.group5.sebmnotificationservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知事件枚举
 * 定义具体的通知场景及其对应的通知节点
 */
@Getter
@AllArgsConstructor
public enum NotificationEventEnum {
    /**
     * 租借审批通过
     */
    BORROW_APPLICATION_APPROVED(1002, "租借成功", NotificationNodeEnum.BORROW_SUCCESS),

    /**
     * 即将逾期通知
     */
    UPCOMING_OVERDUE_NOTICE(1003, "即将逾期通知", NotificationNodeEnum.DUE_DATE_REMINDER),

    /**
     * 到期通知
     */
    DUE_DATE_NOTICE(1004, "到期通知", NotificationNodeEnum.DUE_DATE_REMINDER);

    /**
     * 事件代码
     */
    private final Integer code;

    /**
     * 事件描述
     */
    private final String description;

    /**
     * 通知节点代码
     */
    private final NotificationNodeEnum notificationNode;

    /**
     * 根据代码获取枚举
     */
    public static NotificationEventEnum parseCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (NotificationEventEnum eventEnum : NotificationEventEnum.values()) {
            if (eventEnum.getCode().equals(code)) {
                return eventEnum;
            }
        }
        return null;
    }

    /**
     * 根据通知节点获取枚举
     */
    public static NotificationEventEnum parseByNode(Integer notificationNode) {
        if (notificationNode == null) {
            return null;
        }
        for (NotificationEventEnum eventEnum : NotificationEventEnum.values()) {
            if (eventEnum.getNotificationNode().equals(notificationNode)) {
                return eventEnum;
            }
        }
        return null;
    }

    /**
     * 验证是否为可用的事件代码
     */
    public static boolean isValidCode(Integer code) {
        return parseCode(code) != null;
    }

    /**
     * 验证是否为可用的通知节点
     */
    public static boolean isValidNode(Integer notificationNode) {
        return parseByNode(notificationNode) != null;
    }

    /**
     * 根据通知节点获取对应的事件代码
     */
    public static Integer getCodeByNotificationNode(Integer notificationNode) {
        NotificationEventEnum eventEnum = parseByNode(notificationNode);
        return eventEnum != null ? eventEnum.getCode() : null;
    }
}
