package com.group5.sebmnotificationservice.enums;

import cn.hutool.core.collection.CollectionUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 通知方式枚举
 */
@Getter
@AllArgsConstructor
public enum NotificationMethodEnum {

    EMAIL(1, "邮件"),
    // todo 短信或者whatapp, 后期视情况而定
    SMS(2, "短信"),
    INTERNAL_MSG(3, "站内信");

    private final Integer code;
    private final String description;

    /**
     * 根据code获取枚举
     */
    public static NotificationMethodEnum parseMethod(Integer code) {
        if (code == null) {
            return null;
        }
        for (NotificationMethodEnum methodEnum : NotificationMethodEnum.values()) {
            if (methodEnum.getCode().equals(code)) {
                return methodEnum;
            }
        }
        return null;
    }

    /**
     * 验证code是否有效
     */
    public static boolean isValidCode(Integer code) {
        return parseMethod(code) != null;
    }

    /**
     * 验证code是否有效(多个参数版)
     */
    public static boolean isValidCode(List<Integer> methods) {
        if(CollectionUtil.isEmpty(methods)) {
            return false;
        }
        for (Integer method : methods) {
            if(!isValidCode(method)) {
                return false;
            }
        }
        return true;
    }

}
