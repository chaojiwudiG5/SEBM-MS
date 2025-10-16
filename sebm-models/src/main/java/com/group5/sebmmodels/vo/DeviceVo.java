package com.group5.sebmmodels.vo;

import lombok.Data;

@Data
public class DeviceVo {
  private Long id;

  private String deviceName;

  private String deviceType;

  private Integer status;

  private String location;

  private String description;

  private String image;
}
