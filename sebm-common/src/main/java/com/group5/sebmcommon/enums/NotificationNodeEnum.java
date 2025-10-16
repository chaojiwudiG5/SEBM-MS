package com.group5.sebmcommon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知节点枚举
 */
@Getter
@AllArgsConstructor
public enum NotificationNodeEnum {
    
    /**
     * 租借成功
     */
    BORROW_SUCCESS(1, "租借审批成功"),
    
    /**
     * 到期提醒
     */
    DUE_DATE_REMINDER(3, "到期提醒");
    
    /**
     * 租借审批不成功
     */
    //BORROW_APPROVAL_FAILED(-1, "租借审批不成功");
    
    /**
     * 节点代码
     */
    private final Integer code;
    
    /**
     * 节点描述
     */
    private final String description;
    
    /**
     * 根据code获取枚举
     */
    public static NotificationNodeEnum parseNode(Integer code) {
        if (code == null) {
            return null;
        }
        for (NotificationNodeEnum nodeEnum : NotificationNodeEnum.values()) {
            if (nodeEnum.getCode().equals(code)) {
                return nodeEnum;
            }
        }
        return null;
    }
    
    /**
     * 验证是否为可用节点
     */
    public static boolean isValidCode(Integer code) {
        return parseNode(code) != null;
    }

}
