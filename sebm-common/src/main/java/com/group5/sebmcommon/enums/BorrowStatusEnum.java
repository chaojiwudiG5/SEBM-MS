package com.group5.sebmcommon.enums;

import lombok.Getter;

/**
 * 借用记录状态枚举
 */
@Getter
public enum BorrowStatusEnum {

    /**
     * 已借出
     */
    BORROWED(0, "Borrowed"),

    /**
     * 已归还
     */
    RETURNED(1, "Returned"),

    /**
     * 逾期未归还
     */
    OVERDUE(2, "Overdue");

    private final int code;
    private final String description;

    BorrowStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据 code 获取枚举
     */
    public static BorrowStatusEnum fromCode(int code) {
        for (BorrowStatusEnum status : BorrowStatusEnum.values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown borrow status code: " + code);
    }
}
