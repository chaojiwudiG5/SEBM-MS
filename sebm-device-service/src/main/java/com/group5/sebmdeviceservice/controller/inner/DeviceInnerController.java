package com.group5.sebmdeviceservice.controller.inner;

import com.group5.sebmdeviceservice.service.services.DeviceService;
import com.group5.sebmmodels.entity.DevicePo;
import com.group5.sebmmodels.vo.DeviceVo;
import com.group5.sebmserviceclient.service.DeviceFeignClient;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inner")
public class DeviceInnerController implements DeviceFeignClient {

  @Resource
  private DeviceService deviceService;

  @Override
  public DevicePo getById(Long id) {
    return deviceService.getById(id);
  }

  @Override
  public List<DevicePo> listByIds(Collection<Long> ids) {
    return deviceService.listByIds(ids);
  }

  @Override
  public DeviceVo updateDeviceStatus(Long deviceId, Integer status) {
    return deviceService.updateDeviceStatus(deviceId, status);
  }

  @Override
  public DeviceVo getDeviceById(Long id) {
    return deviceService.getDeviceById(id);
  }
}
