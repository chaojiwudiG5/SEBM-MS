package com.group5.sebmcommon.enums;

import lombok.Getter;

/**
 * Device status enum
 * 0 - Available
 * 1 - Borrowed
 * 2 - Maintenance
 * 3 - Retired
 */
@Getter
public enum DeviceStatusEnum {

    AVAILABLE(0, "Available"),
    BORROWED(1, "Borrowed"),
    MAINTENANCE(2, "Maintenance"),
    BROKEN(3, "Broken");

    private final int code;
    private final String description;

    DeviceStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static DeviceStatusEnum fromCode(int code) {
        for (DeviceStatusEnum status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid DeviceStatus code: " + code);
    }
}
