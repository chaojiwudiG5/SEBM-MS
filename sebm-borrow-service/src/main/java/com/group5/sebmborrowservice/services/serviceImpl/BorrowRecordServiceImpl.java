package com.group5.sebmborrowservice.services.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.group5.sebmborrowservice.dao.BorrowRecordMapper;
import com.group5.sebmborrowservice.services.BorrowRecordService;
import com.group5.sebmcommon.constant.BorrowConstant;
import com.group5.sebmcommon.enums.BorrowStatusEnum;
import com.group5.sebmcommon.enums.DeviceStatusEnum;
import com.group5.sebmcommon.exception.BusinessException;
import com.group5.sebmcommon.exception.ErrorCode;
import com.group5.sebmcommon.exception.ThrowUtils;
import com.group5.sebmcommon.utils.GeoFenceUtils;
import com.group5.sebmmodels.dto.borrow.BorrowRecordAddDto;
import com.group5.sebmmodels.dto.borrow.BorrowRecordDto;
import com.group5.sebmmodels.dto.borrow.BorrowRecordQueryDto;
import com.group5.sebmmodels.dto.borrow.BorrowRecordQueryWithStatusDto;
import com.group5.sebmmodels.dto.borrow.BorrowRecordReturnDto;
import com.group5.sebmmodels.dto.user.UserDto;
import com.group5.sebmmodels.entity.BorrowRecordPo;
import com.group5.sebmmodels.entity.DevicePo;
import com.group5.sebmmodels.vo.BorrowRecordVo;
import com.group5.sebmserviceclient.service.DeviceFeignClient;
import com.group5.sebmserviceclient.service.NotificationFeignClient;
import com.group5.sebmserviceclient.service.UserFeignClient;
import jakarta.annotation.Resource;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Luoimo
 * @description 针对表【borrowRecord(设备借用记录表)】的数据库操作Service实现
 * @createDate 2025-09-26 11:27:18
 */
@Service
@Slf4j
public class BorrowRecordServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecordPo>
    implements BorrowRecordService {

  @Resource
  private BorrowRecordMapper borrowRecordMapper;

  @Resource
  private UserFeignClient borrowerService;

  @Resource
  private DeviceFeignClient deviceService;

  @Resource
  private NotificationFeignClient notificationService;


  @Override
  public BorrowRecordDto getBorrowRecordById(Long borrowRecordId) {
    BorrowRecordPo borrowRecordPo = borrowRecordMapper.selectById(borrowRecordId);
    ThrowUtils.throwIf(borrowRecordPo == null, ErrorCode.NOT_FOUND_ERROR,
        "Borrow record not found");
    BorrowRecordDto borrowRecordDto = new BorrowRecordDto();
    BeanUtils.copyProperties(borrowRecordPo, borrowRecordDto);
    return borrowRecordDto;
  }

  @Override
  @Transactional(rollbackFor = BusinessException.class)
  public BorrowRecordVo borrowDevice(BorrowRecordAddDto borrowRecordAddDto,
      Long userId) {
    //1. 获取当前用户
    UserDto currentUser = borrowerService.getCurrentUserDtoFromID(userId);
    //2. 检查时间区间是否合法
    //2.1 借出时间不能早于当前时间，加上10秒以防止时间误差
    ThrowUtils.throwIf(
        borrowRecordAddDto.getBorrowTime().getTime() + 1000 * 10 < System.currentTimeMillis(),
        ErrorCode.PARAMS_ERROR, "Borrow time cannot be earlier than current time");
    //2.2 应还时间不能早于借出时间
    ThrowUtils.throwIf(
        borrowRecordAddDto.getDueTime().getTime() < borrowRecordAddDto.getBorrowTime()
            .getTime(), ErrorCode.PARAMS_ERROR, "Due time cannot be earlier than borrow time");
    //2.3 应还时间不能超过7天
    ThrowUtils.throwIf(borrowRecordAddDto.getDueTime().getTime() - System.currentTimeMillis()
            > BorrowConstant.MAX_BORROW_DAYS * 24 * 60 * 60 * 1000,
        ErrorCode.PARAMS_ERROR, "Due time cannot be later than 7 days");
    //3. 判断用户是否还能借用设备
    ThrowUtils.throwIf(currentUser.getOverdueTimes() >= currentUser.getMaxOverdueTimes(),
        ErrorCode.NO_AUTH_ERROR, "Overdue times exceed the limit");
    ThrowUtils.throwIf(
        currentUser.getBorrowedDeviceCount() >= currentUser.getMaxBorrowedDeviceCount(),
        ErrorCode.NO_AUTH_ERROR, "Borrowed device count exceed the limit");
    //4. 判断设备是否存在
    //4. 修改设备状态
    DevicePo device = deviceService.getById(borrowRecordAddDto.getDeviceId());
    ThrowUtils.throwIf(device == null, ErrorCode.NOT_FOUND_ERROR, "Device not found");
    ThrowUtils.throwIf(device.getStatus() != DeviceStatusEnum.AVAILABLE.getCode(),
        ErrorCode.PARAMS_ERROR, "Device is not available");
    deviceService.updateDeviceStatus(borrowRecordAddDto.getDeviceId(),
        DeviceStatusEnum.BORROWED.getCode());
    //5. 保存记录
    BorrowRecordPo borrowRecord = new BorrowRecordPo();
    BeanUtils.copyProperties(borrowRecordAddDto, borrowRecord);
    borrowRecord.setUserId(currentUser.getId());
    borrowRecordMapper.insert(borrowRecord);
    //6. 增加用户借用设备数量
    borrowerService.updateBorrowedCount(currentUser.getId(), BorrowConstant.PLUS);
    BorrowRecordVo borrowRecordVo = new BorrowRecordVo();
    BeanUtils.copyProperties(borrowRecord, borrowRecordVo);
    //TODO 7. 发送通知
    sendBorrowSuccessNotification(borrowRecord, device, currentUser);
    return borrowRecordVo;
  }


  @Override
  public Page<BorrowRecordVo> getBorrowRecordList(BorrowRecordQueryDto borrowRecordQueryDto) {
    //1. 根据userId查询记录
    Page<BorrowRecordPo> page = new Page<>(borrowRecordQueryDto.getPageNumber(),
        borrowRecordQueryDto.getPageSize());
    //2. 分页查询
    Page<BorrowRecordPo> recordPage = borrowRecordMapper.selectPage(page,
        new QueryWrapper<BorrowRecordPo>().eq("userId", borrowRecordQueryDto.getUserId()));
    List<Long> deviceIds = recordPage.getRecords().stream().map(BorrowRecordPo::getDeviceId)
        .toList();
    //3. 查询设备信息
    List<DevicePo> deviceList = deviceService.listByIds(deviceIds);
    //4. 转换结果
    Page<BorrowRecordVo> resultPage = new Page<>();
    BeanUtils.copyProperties(recordPage, resultPage);
    resultPage.setRecords(recordPage.getRecords().stream().map(borrowRecord -> {
      BorrowRecordVo borrowRecordVo = new BorrowRecordVo();
      BeanUtils.copyProperties(borrowRecord, borrowRecordVo);
      DevicePo device = deviceList.stream()
          .filter(d -> d.getId().longValue() == borrowRecord.getDeviceId().longValue())
          .findFirst().orElse(null);
      if (device != null) {
        borrowRecordVo.setDeviceName(device.getDeviceName());
        borrowRecordVo.setImage(device.getImage());
      }
      return borrowRecordVo;
    }).toList());
    return resultPage;
  }

  @Override
  public Page<BorrowRecordVo> getBorrowRecordListWithStatus(
      BorrowRecordQueryWithStatusDto borrowRecordQueryWithStatusDto) {
    //1. 根据userId和status查询记录
    Page<BorrowRecordPo> page = new Page<>(borrowRecordQueryWithStatusDto.getPageNumber(),
        borrowRecordQueryWithStatusDto.getPageSize());
    //2. 分页查询
    Page<BorrowRecordPo> recordPage = borrowRecordMapper.selectPage(page,
        new QueryWrapper<BorrowRecordPo>()
            .eq("userId", borrowRecordQueryWithStatusDto.getUserId())
            .eq("status", borrowRecordQueryWithStatusDto.getStatus()));
    List<Long> deviceIds = recordPage.getRecords().stream().map(BorrowRecordPo::getDeviceId)
        .toList();
    //3. 查询设备信息
    List<DevicePo> deviceList = deviceService.listByIds(deviceIds);
    //4. 转换结果
    Page<BorrowRecordVo> resultPage = new Page<>();
    BeanUtils.copyProperties(recordPage, resultPage);
    resultPage.setRecords(recordPage.getRecords().stream().map(borrowRecord -> {
      BorrowRecordVo borrowRecordVo = new BorrowRecordVo();
      BeanUtils.copyProperties(borrowRecord, borrowRecordVo);
      DevicePo device = deviceList.stream()
          .filter(d -> d.getId().longValue() == borrowRecord.getDeviceId().longValue())
          .findFirst()
          .orElse(null);
      if (device != null) {
        borrowRecordVo.setDeviceName(device.getDeviceName());
        borrowRecordVo.setImage(device.getImage());
      }
      return borrowRecordVo;
    }).toList());
    return resultPage;
  }

  @Override
  @Transactional(rollbackFor = BusinessException.class)
  public BorrowRecordVo returnDevice(BorrowRecordReturnDto borrowRecordReturnDto,
      Long userId) {
    //1. 校验参数
    UserDto currentUser = borrowerService.getCurrentUserDtoFromID(userId);
    ThrowUtils.throwIf(currentUser == null, ErrorCode.NOT_FOUND_ERROR, "No user");
//    boolean inGeofence = GeoFenceUtils.isInGeofence(borrowRecordReturnDto.getLongitude(),
//        borrowRecordReturnDto.getLatitude(), BorrowConstant.CENTER_LONGITUDE_SCHOOL,
//        BorrowConstant.CENTER_LATITUDE_SCHOOL, BorrowConstant.RADIUS);
//    ThrowUtils.throwIf(!inGeofence, ErrorCode.FORBIDDEN_ERROR,
//        "Out of geofence,please return in the storeroom");
    //2. 更新记录
    BorrowRecordPo borrowRecord = borrowRecordMapper.selectById(borrowRecordReturnDto.getId());
    ThrowUtils.throwIf(userId.longValue() != borrowRecord.getUserId().longValue(),
        ErrorCode.FORBIDDEN_ERROR, "No permission");
    ThrowUtils.throwIf(borrowRecord == null, ErrorCode.NOT_FOUND_ERROR, "No borrow record");
    borrowRecord.setReturnTime(borrowRecordReturnDto.getReturnTime());
    borrowRecord.setStatus(BorrowStatusEnum.RETURNED.getCode());
    if (borrowRecordReturnDto.getRemarks() != null) {
      borrowRecord.setRemarks(borrowRecordReturnDto.getRemarks());
    }
    borrowRecordMapper.updateById(borrowRecord);
    //3. 更新设备状态
    DevicePo device = deviceService.getById(borrowRecord.getDeviceId());
    ThrowUtils.throwIf(device.getStatus() != DeviceStatusEnum.BORROWED.getCode(),
        ErrorCode.PARAMS_ERROR, "Device is not borrowed");
    ThrowUtils.throwIf(device == null, ErrorCode.NOT_FOUND_ERROR, "No device");
    deviceService.updateDeviceStatus(borrowRecord.getDeviceId(),
        DeviceStatusEnum.AVAILABLE.getCode());
    //4. 减少用户借用设备数量
    borrowerService.updateBorrowedCount(currentUser.getId(), BorrowConstant.MINUS);
    BorrowRecordVo borrowRecordVo = new BorrowRecordVo();
    BeanUtils.copyProperties(borrowRecord, borrowRecordVo);
    //TODO发送通知
    sendReturnSuccessNotification(borrowRecord, device, currentUser);
    return borrowRecordVo;
  }

  /**
   * 发送借用成功通知
   */
  private void sendBorrowSuccessNotification(BorrowRecordPo borrowRecord, DevicePo device,
      UserDto user) {
    try {
      // 构建模板变量
      java.util.Map<String, Object> templateVars = new java.util.HashMap<>();
      templateVars.put("userName", user.getUsername());
      templateVars.put("deviceName", device.getDeviceName());
      templateVars.put("deviceId", device.getId());
      templateVars.put("borrowTime", borrowRecord.getBorrowTime());
      templateVars.put("dueTime", borrowRecord.getDueTime());
      templateVars.put("borrowRecordId", borrowRecord.getId());

      // 构建发送通知DTO
      com.group5.sebmmodels.dto.notification.SendNotificationDto sendNotificationDto = 
          com.group5.sebmmodels.dto.notification.SendNotificationDto.builder()
              .notificationEvent(
                  com.group5.sebmcommon.enums.NotificationEventEnum.BORROW_APPLICATION_APPROVED.getCode())
              .userId(user.getId())
              .templateVars(templateVars)
                  // 即时通知不需要传入时间戳
             // .nodeTimestamp(System.currentTimeMillis() / 1000) // 当前时间戳（秒）
              .build();

      // 发送通知
      Boolean success = notificationService.sendNotification(sendNotificationDto);
      log.info("借用成功通知发送结果: {}, userId: {}, deviceId: {}, borrowRecordId: {}",
          success, user.getId(), device.getId(), borrowRecord.getId());

    } catch (Exception e) {
      log.error("发送借用成功通知失败: userId={}, deviceId={}, error={}",
          user.getId(), device.getId(), e.getMessage(), e);
      // 通知发送失败不影响主业务流程，只记录日志
    }
  }

  /**
   * 发送归还成功通知
   */
  private void sendReturnSuccessNotification(BorrowRecordPo borrowRecord, DevicePo device,
      UserDto user) {
    try {
      // 构建模板变量
      java.util.Map<String, Object> templateVars = new java.util.HashMap<>();
      templateVars.put("userName", user.getUsername());
      templateVars.put("deviceName", device.getDeviceName());
      templateVars.put("deviceId", device.getId());
      templateVars.put("borrowTime", borrowRecord.getBorrowTime());
      templateVars.put("returnTime", borrowRecord.getReturnTime());
      templateVars.put("borrowRecordId", borrowRecord.getId());

      // 构建发送通知DTO
      com.group5.sebmmodels.dto.notification.SendNotificationDto sendNotificationDto = 
          com.group5.sebmmodels.dto.notification.SendNotificationDto.builder()
              .notificationEvent(
                  com.group5.sebmcommon.enums.NotificationEventEnum.RETURN_SUCCESS.getCode())
              .userId(user.getId())
              .templateVars(templateVars)
                  // 即时通知不需要传入时间戳
              //.nodeTimestamp(System.currentTimeMillis() / 1000)
              .build();

      // 发送通知
      Boolean success = notificationService.sendNotification(sendNotificationDto);
      log.info("归还成功通知发送结果: {}, userId: {}, deviceId: {}, borrowRecordId: {}",
          success, user.getId(), device.getId(), borrowRecord.getId());

    } catch (Exception e) {
      log.error("发送归还成功通知失败, userId: {}, deviceId: {}, borrowRecordId: {}, error: {}",
          user.getId(), device.getId(), borrowRecord.getId(), e.getMessage(), e);
      // 通知发送失败不影响主业务流程，只记录日志
    }
  }


}




