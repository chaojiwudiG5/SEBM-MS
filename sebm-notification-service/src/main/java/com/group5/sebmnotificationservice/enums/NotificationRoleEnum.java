package com.group5.sebmnotificationservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知角色枚举
 */
@Getter
@AllArgsConstructor
public enum NotificationRoleEnum {
    Admin(0,"管理员"),
    Borrower(1,"借用人"),
    Technican(2,"技工");
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
