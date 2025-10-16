package com.group5.sebmmodels.dto.device;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DeviceUpdateDto {
  @NotNull(message = "设备ID不能为空")
  private Long id;

  @NotBlank(message = "设备名称不能为空")
  @Size(max = 50, message = "设备名称不能超过50字符")
  private String deviceName;

  @NotBlank(message = "设备类型不能为空")
  @Size(max = 20, message = "设备类型不能超过20字符")
  private String deviceType;

  @NotNull(message = "设备状态不能为空")
  @Min(value = 0, message = "设备状态最小为0")
  @Max(value = 3, message = "设备状态最大为3")
  private Integer status;

  @NotBlank(message = "存放位置不能为空")
  @Size(max = 100, message = "存放位置不能超过100字符")
  private String location;

  @Size(max = 200, message = "设备描述不能超过200字符")
  private String description;

  @Size(max = 200, message = "设备图片不能超过200字符")
  private String image;
}
