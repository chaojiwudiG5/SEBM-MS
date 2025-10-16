package com.group5.sebmmaintenanceservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.group5.sebmcommon.BaseResponse;
import com.group5.sebmcommon.ResultUtils;
import com.group5.sebmcommon.annotation.AuthCheck;
import com.group5.sebmcommon.enums.UserRoleEnum;
import com.group5.sebmcommon.exception.ErrorCode;
import com.group5.sebmcommon.exception.ThrowUtils;
import com.group5.sebmmaintenanceservice.service.services.MechanicanMaintenanceRecordService;
import com.group5.sebmmodels.dto.maintenance.MechanicRecordQueryDto;
import com.group5.sebmmodels.dto.maintenance.MechanicanQueryDto;
import com.group5.sebmmodels.dto.maintenance.MechanicanUpdateDto;
import com.group5.sebmmodels.vo.MechanicanMaintenanceRecordVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "MechanicanMaintenanceRecord")
@RequestMapping("/mechanicanMaintenanceRecord")
@AllArgsConstructor
public class MechanicanMaintenanceRecordController {

  private final MechanicanMaintenanceRecordService mechanicanMaintenanceRecordService;

  @PostMapping("/add")
  @AuthCheck(mustRole = UserRoleEnum.ADMIN)
  public BaseResponse<Long> addMaintenanceTask(Long userMaintenanceRecordId, Long mechanicId) {
    Long recordId = mechanicanMaintenanceRecordService.addMaintenanceTask(mechanicId,
        userMaintenanceRecordId);
    log.info("Mechanic {} added maintenance task {}", mechanicId, recordId);
    return ResultUtils.success(recordId);
  }

  @PostMapping("/myList")
  @AuthCheck(mustRole = UserRoleEnum.TECHNICIAN)
  public BaseResponse<Page<MechanicanMaintenanceRecordVo>> listMyTasks(
      @RequestBody @Valid MechanicanQueryDto queryDto, HttpServletRequest request) {
    Long mechanicId = (Long) request.getAttribute("userId");
    ThrowUtils.throwIf(mechanicId == null, ErrorCode.NOT_LOGIN_ERROR, "lopgin required");
    Page<MechanicanMaintenanceRecordVo> page = mechanicanMaintenanceRecordService
        .listMechanicMaintenanceRecords(mechanicId, queryDto);
    log.info("Mechanic {} queried maintenance tasks page {}", mechanicId, page);
    return ResultUtils.success(page);
  }


  @PostMapping("/getRecordDetail")
  public BaseResponse<MechanicanMaintenanceRecordVo> getRecordDetail(
      @RequestBody @Valid MechanicRecordQueryDto queryDto) {
    MechanicanMaintenanceRecordVo record = mechanicanMaintenanceRecordService.getMechanicMaintenanceRecordDetail(queryDto);
    log.info("Mechanic queried maintenance task detail {}", record);
    return ResultUtils.success(record);
  }

  @PostMapping("/updateStatus")
  public BaseResponse<Boolean> updateTaskStatus(@RequestBody @Valid MechanicanUpdateDto updateDto,
      HttpServletRequest request) {
    Long mechanicId = (Long) request.getAttribute("userId");
    ThrowUtils.throwIf(mechanicId == null, ErrorCode.NOT_LOGIN_ERROR, "login required");
    Boolean result = mechanicanMaintenanceRecordService
        .updateMechanicMaintenanceRecord(mechanicId, updateDto);
    log.info("Mechanic {} updated maintenance task {} to status {}", mechanicId, updateDto.getId(),
        updateDto.getStatus());
    return ResultUtils.success(result);
  }
}