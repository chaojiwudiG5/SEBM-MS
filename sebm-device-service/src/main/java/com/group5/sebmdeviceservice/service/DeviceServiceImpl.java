package com.group5.sebmdeviceservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.group5.sebmcommon.exception.BusinessException;
import com.group5.sebmcommon.exception.ErrorCode;
import com.group5.sebmdeviceservice.dao.DeviceMapper;
import com.group5.sebmdeviceservice.service.services.DeviceService;
import com.group5.sebmmodels.dto.common.DeleteDto;
import com.group5.sebmmodels.dto.device.DeviceAddDto;
import com.group5.sebmmodels.dto.device.DeviceQueryDto;
import com.group5.sebmmodels.dto.device.DeviceUpdateDto;
import com.group5.sebmmodels.entity.DevicePo;
import com.group5.sebmmodels.vo.DeviceVo;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;


/**
 * @author Luoimo
 * @description 针对表【device(设备表)】的数据库操作Service实现
 * @createDate 2025-09-26 11:29:28
 */
@Service
@AllArgsConstructor
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, DevicePo>
    implements DeviceService {

  private final DeviceMapper deviceMapper;

  /**
   * 获取设备列表
   *
   * @param deviceQueryDto
   * @return Page<DeviceVo>
   */
  @Override
//  @Cacheable(value = "deviceList", key = "#deviceQueryDto.toString()+ '_' + #deviceQueryDto.pageNumber + '_' + #deviceQueryDto.pageSize")
  public Page<DeviceVo> getDeviceList(DeviceQueryDto deviceQueryDto) {
    // 1. 获取分页参数
    int pageNum = deviceQueryDto.getPageNumber();
    int pageSize = deviceQueryDto.getPageSize();

    // 2. 构建分页对象
    Page<DevicePo> page = new Page<>(pageNum, pageSize);

    // 3. 构建筛选条件
    QueryWrapper<DevicePo> queryWrapper = new QueryWrapper<>();
    if (deviceQueryDto.getDeviceName() != null) {
      queryWrapper.like("deviceName", deviceQueryDto.getDeviceName());
    }
    if (deviceQueryDto.getDeviceType() != null) {
      queryWrapper.like("deviceType", deviceQueryDto.getDeviceType());
    }
    if (deviceQueryDto.getStatus() != null) {
      queryWrapper.eq("status", deviceQueryDto.getStatus());
    }
    if (deviceQueryDto.getLocation() != null) {
      queryWrapper.like("location", deviceQueryDto.getLocation());
    }
    // 4. 执行查询
    Page<DevicePo> devicePage = deviceMapper.selectPage(page, queryWrapper);

    // 4. 转换为 VO 对象
    List<DeviceVo> deviceVoList = devicePage.getRecords().stream()
        .map(device -> {
          DeviceVo vo = new DeviceVo();
          BeanUtils.copyProperties(device, vo); // 复制属性
          return vo;
        })
        .collect(Collectors.toList());

    // 5. 封装成新的分页对象（Page<DeviceVo>）
    Page<DeviceVo> resultPage = new Page<>(pageNum, pageSize, devicePage.getTotal());
    resultPage.setRecords(deviceVoList);

    return resultPage;
  }

  /**
   * 根据id获取设备
   *
   * @param id
   * @return DeviceVo
   */
  @Override
//  @Cacheable(value = "deviceById", key = "#id")
  public DeviceVo getDeviceById(Long id) {
    //1. 根据id查询设备
    DevicePo device = deviceMapper.selectById(id);
    //2. 转换为 VO 对象
    DeviceVo vo = new DeviceVo();
    BeanUtils.copyProperties(device, vo); // 复制属性
    return vo;
  }

  /**
   * 新增设备
   *
   * @param deviceAddDto
   * @return 成功返回id，失败返回null
   */
  @Override
//  @Caching(evict = {
//      @CacheEvict(value = "deviceList", allEntries = true) // 清除列表缓存
//  })
  public Long addDevice(DeviceAddDto deviceAddDto) {
    //1. 转换为实体对象
    DevicePo device = new DevicePo();
    BeanUtils.copyProperties(deviceAddDto, device); // 复制属性
    //2. 插入数据库
    deviceMapper.insert(device);
    //3. 返回id
    return device.getId();
  }

  /**
   * 更新设备
   *
   * @param deviceUpdateDto
   * @return 成功返回id，失败返回null
   */
  @Override
//  @Caching(
//      put = {
//          @CachePut(value = "deviceById", key = "#deviceUpdateDto.id")
//      },
//      evict = {
//          @CacheEvict(value = "deviceList", allEntries = true)
//      }
//  )
  public DeviceVo updateDevice(DeviceUpdateDto deviceUpdateDto) {
    //1. 转换为实体对象
    DevicePo device = new DevicePo();
    BeanUtils.copyProperties(deviceUpdateDto, device); // 复制属性
    //2. 更新数据库
    deviceMapper.updateById(device);
    //3. 返回 VO 对象
    DeviceVo vo = new DeviceVo();
    BeanUtils.copyProperties(device, vo); // 复制属性
    return vo;
  }

  /**
   * 根据id删除设备
   *
   * @param deleteDto
   * @return 成功返回true，失败返回false
   */
  @Override
//  @Caching(evict = {
//      @CacheEvict(value = "deviceById", key = "#deviceUpdateDto.id"),
//      @CacheEvict(value = "deviceList", allEntries = true) // 清除列表缓存
//  })
  public Boolean deleteDevice(DeleteDto deleteDto) {
    //1. 根据id删除设备
    try {
      deviceMapper.deleteById(deleteDto.getId());
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Delete device failed");
    }
    //2. 返回成功
    return true;
  }

  /**
   * 更新设备状态
   *
   * @param deviceId
   * @param status
   * @return
   */
  @Override
//  @Caching(
//      put = {
//          @CachePut(value = "deviceById", key = "#deviceId")
//      },
//      evict = {
//          @CacheEvict(value = "deviceList", allEntries = true)
//      }
//  )
  public DeviceVo updateDeviceStatus(Long deviceId, Integer status) {
    //1. 根据id查询设备
    DevicePo device = deviceMapper.selectById(deviceId);
    //2. 更新设备状态
    device.setStatus(status);
    //3. 更新数据库
    deviceMapper.updateById(device);
    //4. 返回成功
    DeviceVo vo = new DeviceVo();
    BeanUtils.copyProperties(device, vo); // 复制属性
    return vo;
  }


}




