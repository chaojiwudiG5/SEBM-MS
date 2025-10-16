package com.group5.sebmcommon.enums;

import lombok.Getter;

/**
 * User role enum
 * 0 - User
 * 1 - Admin
 * 2 - Technician
 */
@Getter
public enum UserRoleEnum {

  USER(0, "User"),
  ADMIN(1, "Admin"),
  TECHNICIAN(2, "Technician");

  private final int code;
  private final String description;

  UserRoleEnum(int code, String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * 根据 code 获取枚举
   */
  public static UserRoleEnum fromCode(int code) {
    for (UserRoleEnum role : values()) {
      if (role.getCode() == code) {
        return role;
      }
    }
    throw new IllegalArgumentException("Invalid UserRole code: " + code);
  }
}
