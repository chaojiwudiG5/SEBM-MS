package com.group5.sebmmodels.dto.device;

import com.group5.sebmmodels.dto.common.PageDto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceQueryDto extends PageDto {
  @Size(max = 50, message = "设备名称不能超过50字符")
  private String deviceName;

  @Size(max = 20, message = "设备类型不能超过20字符")
  private String deviceType;

  @Min(value = 0, message = "设备状态最小为0")
  @Max(value = 3, message = "设备状态最大为3")
  private Integer status;

  @Size(max = 100, message = "存放位置不能超过100字符")
  private String location;
}
