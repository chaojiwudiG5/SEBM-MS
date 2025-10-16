package com.group5.sebmcommon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知角色枚举
 */
@Getter
@AllArgsConstructor
public enum NotificationRoleEnum {
    Admin(0,"Admin"),
    Borrower(1,"Borrower"),
    Technican(2,"Technican");
    private final Integer code;
    private final String description;


    public static NotificationRoleEnum parseRole(Integer code) {
        for(NotificationRoleEnum role : NotificationRoleEnum.values()) {
            if(role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }

    /**
     * 验证是否为可用节点
     */
    public static boolean isValidCode(Integer code) {
        return parseRole(code) != null;
    }
}
