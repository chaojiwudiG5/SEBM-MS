package com.group5.sebmmodels.dto.maintenance;

import java.util.Date;
import lombok.Data;

@Data
public class UserMaintenanceRecordDto {
  private Long id;

  private Long deviceId;

  private Long userId;

  private String description;

  private String image;

  private Integer status;

  private Date createTime;

  private Date updateTime;
}
