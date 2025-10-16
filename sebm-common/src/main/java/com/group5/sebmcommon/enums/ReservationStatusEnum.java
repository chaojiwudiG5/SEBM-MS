package com.group5.sebmcommon.enums;

import lombok.Getter;

@Getter
public enum ReservationStatusEnum {

  NOT_CONFIRMED(0, "Not Confirmed"),
  CONFIRMED(1, "Confirmed"),
  CANCELED(2, "Canceled"),
  OVERDUE(3, "Overdue");

  private final int code;
  private final String description;

  ReservationStatusEnum(int code, String description) {
    this.code = code;
    this.description = description;
  }

  public static ReservationStatusEnum fromCode(int code) {
    for (ReservationStatusEnum status : values()) {
      if (status.getCode() == code) {
        return status;
      }
    }
    throw new IllegalArgumentException("Invalid DeviceStatus code: " + code);
  }
}
