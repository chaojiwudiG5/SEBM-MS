package com.group5.sebmdeviceservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.group5.sebmcommon.BaseResponse;
import com.group5.sebmcommon.ResultUtils;
import com.group5.sebmcommon.annotation.AuthCheck;
import com.group5.sebmcommon.enums.UserRoleEnum;
import com.group5.sebmdeviceservice.service.services.DeviceService;
import com.group5.sebmmodels.dto.common.DeleteDto;
import com.group5.sebmmodels.dto.device.DeviceAddDto;
import com.group5.sebmmodels.dto.device.DeviceQueryDto;
import com.group5.sebmmodels.dto.device.DeviceUpdateDto;
import com.group5.sebmmodels.vo.DeviceVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Device")
@RequestMapping("/")
public class DeviceController {

  @Resource
  private DeviceService deviceService;

  @PostMapping("/getDeviceList")
  public BaseResponse<List<DeviceVo>> getDeviceList(@RequestBody @Valid DeviceQueryDto deviceQueryDto) {
    Page<DeviceVo> deviceVoPage = deviceService.getDeviceList(deviceQueryDto);
    log.info("GetDeviceList called with pageDto: {}, deviceVoPage: {}", deviceQueryDto, deviceVoPage);
    return ResultUtils.success(deviceVoPage.getRecords()); // 返回List
  }

  @GetMapping("/getDevice/{id}")
  public BaseResponse<DeviceVo> getDevice(@PathVariable("id") Long id) {
    DeviceVo deviceVo = deviceService.getDeviceById(id);
    log.info("GetDeviceById called with id: {}, deviceVo: {}", id, deviceVo);
    return ResultUtils.success(deviceVo); // 返回单个DeviceVo
  }

  @PostMapping("/addDevice")
  @AuthCheck(mustRole = UserRoleEnum.ADMIN)
  public BaseResponse<Long> addDevice(@RequestBody @Valid DeviceAddDto deviceAddDto) {
    Long id = deviceService.addDevice(deviceAddDto);
    log.info("AddDevice called with deviceAddDto: {}, id: {}", deviceAddDto, id);
    return ResultUtils.success(id); // 返回新增的id
  }

  @PostMapping("/updateDevice")
  @AuthCheck(mustRole = UserRoleEnum.ADMIN)
  public BaseResponse<DeviceVo> updateDevice(@RequestBody @Valid DeviceUpdateDto deviceUpdateDto) {
    DeviceVo deviceVo = deviceService.updateDevice(deviceUpdateDto);
    log.info("UpdateDevice called with deviceUpdateDto: {}, id: {}", deviceUpdateDto, deviceVo);
    return ResultUtils.success(deviceVo); // 返回新增的id
  }

  @PostMapping("/updateDeviceStatus")
  public BaseResponse<DeviceVo> updateDeviceStatus(Long deviceId, Integer status) {
    DeviceVo result = deviceService.updateDeviceStatus(deviceId, status);
    log.info("UpdateDeviceStatus called with deviceId: {}, status: {}, result: {}", deviceId,
        status, result);
    return ResultUtils.success(result); // 返回更新的状态
  }

  @PostMapping("/deleteDevice")
  @AuthCheck(mustRole = UserRoleEnum.ADMIN)
  public BaseResponse<Boolean> deleteDevice(@RequestBody @Valid DeleteDto deleteDto) {
    Boolean result = deviceService.deleteDevice(deleteDto);
    log.info("DeleteDevice called with deleteDto: {}, result: {}", deleteDto, result);
    return ResultUtils.success(result); // 返回删除的id
  }
}
