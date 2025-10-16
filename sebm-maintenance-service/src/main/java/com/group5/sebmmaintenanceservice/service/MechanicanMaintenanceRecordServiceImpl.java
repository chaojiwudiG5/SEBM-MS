package com.group5.sebmmaintenanceservice.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.group5.sebmcommon.enums.DeviceStatusEnum;
import com.group5.sebmcommon.exception.BusinessException;
import com.group5.sebmcommon.exception.ErrorCode;
import com.group5.sebmcommon.exception.ThrowUtils;
import com.group5.sebmmaintenanceservice.dao.MechanicanMaintenanceRecordMapper;
import com.group5.sebmmaintenanceservice.service.services.MechanicanMaintenanceRecordService;
import com.group5.sebmmaintenanceservice.service.services.UserMaintenanceRecordService;
import com.group5.sebmmodels.dto.maintenance.MechanicRecordQueryDto;
import com.group5.sebmmodels.dto.maintenance.MechanicanQueryDto;
import com.group5.sebmmodels.dto.maintenance.MechanicanUpdateDto;
import com.group5.sebmmodels.dto.maintenance.UserMaintenanceRecordDto;
import com.group5.sebmmodels.entity.MechanicanMaintenanceRecordPo;
import com.group5.sebmmodels.vo.MechanicanMaintenanceRecordVo;
import com.group5.sebmserviceclient.service.DeviceFeignClient;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Luoimo
 * @description 针对表【mechanicanMaintenanceRecord(技工设备维修报单表)】的数据库操作Service实现
 * @createDate 2025-09-26 13:41:31
 */
@Service
public class MechanicanMaintenanceRecordServiceImpl extends
    ServiceImpl<MechanicanMaintenanceRecordMapper, MechanicanMaintenanceRecordPo>
    implements MechanicanMaintenanceRecordService {
  @Resource
  private DeviceFeignClient deviceService;

  @Resource
  private UserMaintenanceRecordService userMaintenanceRecordService;


  @Override
  @Transactional(rollbackFor = BusinessException.class)
  public Long addMaintenanceTask(Long mechanicId, Long userMaintenanceRecordId) {
    //1. 校验参数
    ThrowUtils.throwIf(mechanicId == null, ErrorCode.PARAMS_ERROR, "mechanicId cannot be null");
    ThrowUtils.throwIf(userMaintenanceRecordId == null, ErrorCode.PARAMS_ERROR,
        "userMaintenanceRecordId cannot be null");
    //2. 查询 userMaintenanceRecord
    UserMaintenanceRecordDto userRecord = userMaintenanceRecordService.getUserMaintenanceRecordDto(
        userMaintenanceRecordId);
    ThrowUtils.throwIf(userRecord == null, ErrorCode.NOT_FOUND_ERROR,
        "userMaintenanceRecord not found");
    ThrowUtils.throwIf(userRecord.getStatus() != null && userRecord.getStatus() == 1,
        ErrorCode.OPERATION_ERROR, "userMaintenanceRecord already added");
    //2. 新增 mechanicanMaintenanceRecord
    MechanicanMaintenanceRecordPo record = new MechanicanMaintenanceRecordPo();
    //3. 设置record属性
    record.setUserId(mechanicId);
    record.setDeviceId(userRecord.getDeviceId());
    record.setDescription(userRecord.getDescription());
    record.setImage(userRecord.getImage());
    record.setUserMaintenanceRecordId(userMaintenanceRecordId);
    record.setStatus(1);
    //5. 保存 record
    this.save(record);
    //6. 返回 recordId
    return record.getId();
  }

  @Override
  public Page<MechanicanMaintenanceRecordVo> listMechanicMaintenanceRecords(Long mechanicId,
      MechanicanQueryDto queryDto) {
    Page<MechanicanMaintenanceRecordPo> page = new Page<>(queryDto.getPageNumber(),
        queryDto.getPageSize());
    LambdaQueryWrapper<MechanicanMaintenanceRecordPo> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(MechanicanMaintenanceRecordPo::getUserId, mechanicId)
        .eq(MechanicanMaintenanceRecordPo::getIsDelete, 0)
        .orderByDesc(MechanicanMaintenanceRecordPo::getCreateTime);
    if (queryDto.getDeviceId() != null) {
      wrapper.eq(MechanicanMaintenanceRecordPo::getDeviceId, queryDto.getDeviceId());
    }
    if (queryDto.getStatus() != null) {
      wrapper.eq(MechanicanMaintenanceRecordPo::getStatus, queryDto.getStatus());
    }
    Page<MechanicanMaintenanceRecordPo> poPage = this.page(page, wrapper);
    var voList = poPage.getRecords().stream()
        .map(po -> {
          MechanicanMaintenanceRecordVo vo = new MechanicanMaintenanceRecordVo();
          BeanUtils.copyProperties(po, vo);
          return vo;
        })
        .collect(Collectors.toList());

    // 4) 组装 Page<Vo> 返回
    Page<MechanicanMaintenanceRecordVo> voPage =
        new Page<>(poPage.getCurrent(), poPage.getSize(), poPage.getTotal());
    voPage.setRecords(voList);
    return voPage;
  }

  @Override
  public MechanicanMaintenanceRecordVo getMechanicMaintenanceRecordDetail(
      MechanicRecordQueryDto queryDto) {
    //1. 根据设备ID和状态查询信息
    QueryWrapper<MechanicanMaintenanceRecordPo> wrapper = new QueryWrapper<>();
    wrapper.eq("deviceId", queryDto.getDeviceId());
    wrapper.eq("status", queryDto.getStatus());
    MechanicanMaintenanceRecordPo record = this.getOne(wrapper);
    //2. 组装返回值
    ThrowUtils.throwIf(record == null, ErrorCode.NOT_FOUND_ERROR,
        "mechanicMaintenanceRecord not found");
    MechanicanMaintenanceRecordVo vo = new MechanicanMaintenanceRecordVo();
    BeanUtils.copyProperties(record, vo);
    //3. 返回
    return vo;
  }

  @Override
  @Transactional(rollbackFor = BusinessException.class)
  public Boolean updateMechanicMaintenanceRecord(Long mechanicId, MechanicanUpdateDto updateDto) {
    //1. 校验参数
    MechanicanMaintenanceRecordPo record = this.getById(updateDto.getId());
    ThrowUtils.throwIf(record == null, ErrorCode.NOT_FOUND_ERROR,
        "mechanicanMaintenanceRecord not found");
    ThrowUtils.throwIf(record.getUserId().longValue() != mechanicId.longValue(),
        ErrorCode.FORBIDDEN_ERROR,
        "no permission to update");
    //2. 更新 record
    record.setStatus(updateDto.getStatus());
    record.setUpdateTime(new Date());
    record.setImage(updateDto.getImage());
    record.setDescription(updateDto.getDescription());
    this.updateById(record);
    //3. 更新 userMaintenanceRecord 状态
    userMaintenanceRecordService.updateStatus(updateDto.getUserMaintenanceRecordId(),
        1);
    //4. 更新 device 状态
    if (updateDto.getStatus() == 2) {
      deviceService.updateDeviceStatus(record.getDeviceId(), DeviceStatusEnum.AVAILABLE.getCode());
    }
    if (updateDto.getStatus() == 3) {
      deviceService.updateDeviceStatus(record.getDeviceId(), DeviceStatusEnum.BROKEN.getCode());
    }

    //5. 返回 true
    return true;
  }
}




