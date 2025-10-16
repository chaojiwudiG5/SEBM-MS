package com.group5.sebmmaintenanceservice.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.group5.sebmcommon.enums.DeviceStatusEnum;
import com.group5.sebmcommon.exception.ErrorCode;
import com.group5.sebmcommon.exception.ThrowUtils;
import com.group5.sebmmaintenanceservice.dao.UserMaintenanceRecordMapper;
import com.group5.sebmmaintenanceservice.service.services.UserMaintenanceRecordService;
import com.group5.sebmmodels.dto.borrow.BorrowRecordDto;
import com.group5.sebmmodels.dto.maintenance.UserCreateDto;
import com.group5.sebmmodels.dto.maintenance.UserMaintenanceRecordDto;
import com.group5.sebmmodels.dto.maintenance.UserQueryDto;
import com.group5.sebmmodels.entity.UserMaintenanceRecordPo;
import com.group5.sebmmodels.vo.DeviceVo;
import com.group5.sebmmodels.vo.UserMaintenanceRecordVo;
import com.group5.sebmserviceclient.service.BorrowRecordFeignClient;
import com.group5.sebmserviceclient.service.DeviceFeignClient;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author Luoimo
 * @description 针对表【userMaintenanceRecord(设备维修报单表)】的数据库操作Service实现
 * @createDate 2025-09-26 13:41:45
 */
@Service
public class UserMaintenanceRecordServiceImpl extends
    ServiceImpl<UserMaintenanceRecordMapper, UserMaintenanceRecordPo>
    implements UserMaintenanceRecordService {
  @Resource
  private DeviceFeignClient deviceService;
  @Resource
  private BorrowRecordFeignClient borrowRecordService;

  @Override
  public UserMaintenanceRecordDto updateStatus(Long recordId, Integer status) {
    //1.查询报修单
    UserMaintenanceRecordPo record = this.getById(recordId);
    ThrowUtils.throwIf(record == null, ErrorCode.NOT_FOUND_ERROR,
        "user maintenance record not found");
    //2.校验状态是否合法
    ThrowUtils.throwIf(record.getStatus() != 0, ErrorCode.OPERATION_ERROR,
        "maintenance record cannot be updated");
    //3.修改状态
    record.setStatus(status);
    record.setUpdateTime(new Date());
    boolean success = this.updateById(record);
    ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR,
        "update maintenance record status failed");
    //4.返回DTO
    UserMaintenanceRecordDto dto = new UserMaintenanceRecordDto();
    BeanUtils.copyProperties(record, dto);
    return dto;
  }

  @Override
  public UserMaintenanceRecordDto getUserMaintenanceRecordDto(Long recordId) {
    //1.查询报修单
    UserMaintenanceRecordPo record = this.getById(recordId);
    ThrowUtils.throwIf(record == null, ErrorCode.NOT_FOUND_ERROR,
        "user maintenance record not found");
    //2.转换成DTO
    UserMaintenanceRecordDto dto = new UserMaintenanceRecordDto();
    BeanUtils.copyProperties(record, dto);
    //3.返回DTO
    return dto;
  }

  @Override
  public UserMaintenanceRecordVo createMaintenanceRecord(Long userId, UserCreateDto createDto) {
    //1.校验借用记录是否存在
    BorrowRecordDto borrowRecordDto = borrowRecordService.getBorrowRecordById(
        createDto.getBorrowRecordId());
    //2.校验设备是否被该用户借出
    ThrowUtils.throwIf(userId.longValue() != borrowRecordDto.getUserId().longValue(),
        ErrorCode.NO_AUTH_ERROR, "Device is not borrowed by this user");
    //3.创建报修单
    UserMaintenanceRecordPo record = new UserMaintenanceRecordPo();
    BeanUtils.copyProperties(createDto, record);
    record.setUserId(userId);
    record.setDeviceId(borrowRecordDto.getDeviceId());
    //4.保存报修单
    boolean success = this.save(record);
    ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "create maintenance record failed");
    //5.修改设备状态为维修中
    deviceService.updateDeviceStatus(borrowRecordDto.getDeviceId(),
        DeviceStatusEnum.MAINTENANCE.getCode());
    //6.返回报修单
    UserMaintenanceRecordVo vo = new UserMaintenanceRecordVo();
    BeanUtils.copyProperties(record, vo);
    return vo;
  }

  @Override
  public Page<UserMaintenanceRecordVo> listUserMaintenanceRecords(Long userId,
      UserQueryDto queryDto) {
    //1.分页查询报修单
    QueryWrapper<UserMaintenanceRecordPo> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("userId", userId);
    if (queryDto.getStatus() != null) {
      queryWrapper.eq("status", queryDto.getStatus());
    }
    Page<UserMaintenanceRecordPo> page = this.page(
        new Page<>(queryDto.getPageNumber(), queryDto.getPageSize()), queryWrapper);
    //2.转换成VO
    Page<UserMaintenanceRecordVo> voPage = new Page<>();
    BeanUtils.copyProperties(page, voPage);
    voPage.setRecords(page.getRecords().stream().map(record -> {
      UserMaintenanceRecordVo vo = new UserMaintenanceRecordVo();
      BeanUtils.copyProperties(record, vo);
      DeviceVo deviceVo = deviceService.getDeviceById(record.getDeviceId());
      vo.setDeviceName(deviceVo.getDeviceName());
      return vo;
    }).collect(Collectors.toList()));
    return voPage;
  }

  @Override
  public UserMaintenanceRecordVo getUserMaintenanceRecordDetail(Long userId, Long recordId) {
    LambdaQueryWrapper<UserMaintenanceRecordPo> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(UserMaintenanceRecordPo::getId, recordId)
        .eq(UserMaintenanceRecordPo::getUserId, userId)
        .eq(UserMaintenanceRecordPo::getIsDelete, 0);
    UserMaintenanceRecordPo record = this.getOne(wrapper);
    ThrowUtils.throwIf(record == null, ErrorCode.NOT_FOUND_ERROR,
        "user maintenance record not found");
    UserMaintenanceRecordVo vo = new UserMaintenanceRecordVo();
    BeanUtils.copyProperties(record, vo);
    return vo;
  }

  @Override
  public Boolean cancelMaintenanceRecord(Long userId, Long recordId) {
    LambdaQueryWrapper<UserMaintenanceRecordPo> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(UserMaintenanceRecordPo::getId, recordId)
        .eq(UserMaintenanceRecordPo::getUserId, userId)
        .eq(UserMaintenanceRecordPo::getIsDelete, 0);
    UserMaintenanceRecordPo record = this.getOne(queryWrapper);
    ThrowUtils.throwIf(record == null, ErrorCode.NOT_FOUND_ERROR,
        "user maintenance record not found");
    ThrowUtils.throwIf(!Objects.equals(record.getStatus(), 0), ErrorCode.OPERATION_ERROR,
        "maintenance record cannot be cancelled");
    //1.构建更新条件
    LambdaUpdateWrapper<UserMaintenanceRecordPo> updateWrapper = new LambdaUpdateWrapper<>();
    updateWrapper.eq(UserMaintenanceRecordPo::getId, recordId)
        .eq(UserMaintenanceRecordPo::getUserId, userId);
    //2.逻辑删除报修单
    UserMaintenanceRecordPo update = new UserMaintenanceRecordPo();
    update.setIsDelete(1);
    update.setUpdateTime(new Date());
    boolean success = this.update(update, updateWrapper);
    //3.修改设备状态为可用
    success = success && (deviceService.updateDeviceStatus(record.getDeviceId(), 0) != null);
    ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "delete maintenance record failed");
    return true;
  }

}




