package com.group5.sebmdeviceservice.service.services;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.group5.sebmmodels.dto.common.DeleteDto;
import com.group5.sebmmodels.dto.device.DeviceAddDto;
import com.group5.sebmmodels.dto.device.DeviceQueryDto;
import com.group5.sebmmodels.dto.device.DeviceUpdateDto;
import com.group5.sebmmodels.entity.DevicePo;
import com.group5.sebmmodels.vo.DeviceVo;

/**
 * @author Luoimo
 * @description 针对表【device(设备表)】的数据库操作Service
 * @createDate 2025-09-26 11:29:28
 */
public interface DeviceService extends IService<DevicePo> {

  Page<DeviceVo> getDeviceList(DeviceQueryDto deviceQueryDto);

  DeviceVo getDeviceById(Long id);

  Long addDevice(DeviceAddDto deviceAddDto);

  DeviceVo updateDevice(DeviceUpdateDto deviceUpdateDto);

  DeviceVo updateDeviceStatus(Long deviceId, Integer status);

  Boolean deleteDevice( DeleteDto deleteDto);
}
