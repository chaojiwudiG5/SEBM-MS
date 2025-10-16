package com.group5.sebmserviceclient.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.group5.sebmmodels.dto.common.DeleteDto;
import com.group5.sebmmodels.dto.device.DeviceAddDto;
import com.group5.sebmmodels.dto.device.DeviceQueryDto;
import com.group5.sebmmodels.dto.device.DeviceUpdateDto;
import com.group5.sebmmodels.entity.DevicePo;
import com.group5.sebmmodels.vo.DeviceVo;
import java.util.Collection;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Luoimo
 * @description 针对表【device(设备表)】的数据库操作Service
 * @createDate 2025-09-26 11:29:28
 */
@FeignClient(name = "sebm-device-service", path = "/api/device/inner")
public interface DeviceFeignClient {
  @GetMapping("/getDevice/id")
  DevicePo getById(@RequestParam("id") Long id);

  @GetMapping("/getDevice/ids")
  List<DevicePo> listByIds(@RequestParam("ids") Collection<Long> ids);

  @PostMapping("/updateDevice/status")
  DeviceVo updateDeviceStatus(@RequestParam("deviceId")  Long deviceId, @RequestParam("status") Integer status);

  @GetMapping("/getDeviceVo/id")
  DeviceVo getDeviceById(@RequestParam("id") Long id);
}
