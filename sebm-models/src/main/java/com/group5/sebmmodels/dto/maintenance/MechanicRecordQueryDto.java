package com.group5.sebmmodels.dto.maintenance;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MechanicRecordQueryDto {
  /**
   * 设备筛选
   */
  @NotNull(message = "DeviceId cannot be null.")
  private Long deviceId;

  /**
   * 状态筛选
   */
  @NotNull(message = "Status cannot be null.")
  private Integer status;
}
