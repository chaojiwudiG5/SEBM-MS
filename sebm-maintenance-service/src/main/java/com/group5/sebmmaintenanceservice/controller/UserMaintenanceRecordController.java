package com.group5.sebmmaintenanceservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.group5.sebmcommon.BaseResponse;
import com.group5.sebmcommon.ResultUtils;
import com.group5.sebmcommon.exception.ErrorCode;
import com.group5.sebmcommon.exception.ThrowUtils;
import com.group5.sebmmaintenanceservice.service.services.UserMaintenanceRecordService;
import com.group5.sebmmodels.dto.common.DeleteDto;
import com.group5.sebmmodels.dto.maintenance.UserCreateDto;
import com.group5.sebmmodels.dto.maintenance.UserQueryDto;
import com.group5.sebmmodels.vo.UserMaintenanceRecordVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "UserMaintenanceRecord")
@RequestMapping("/userMaintenanceRecord")
public class UserMaintenanceRecordController {
    @Resource
    private UserMaintenanceRecordService userMaintenanceRecordService;

    @PostMapping("/report")
    public BaseResponse<UserMaintenanceRecordVo> createMaintenanceRecord(@RequestBody @Valid UserCreateDto createDto, HttpServletRequest request)
    {
        Long userId = Long.parseLong(request.getHeader("userId"));
        ThrowUtils.throwIf(userId == null, ErrorCode.NOT_LOGIN_ERROR, "login required");
        UserMaintenanceRecordVo record = userMaintenanceRecordService.createMaintenanceRecord(userId, createDto);
        log.info("User {} created maintenance record {}", userId, record);
        return ResultUtils.success(record);
    }

    @PostMapping("/myList")
    public BaseResponse<List<UserMaintenanceRecordVo>> listMyRecords(@RequestBody @Valid UserQueryDto queryDto, HttpServletRequest request)
    {
        Long userId = Long.parseLong(request.getHeader("userId"));
        ThrowUtils.throwIf(userId == null, ErrorCode.NOT_LOGIN_ERROR, "login required");
        Page<UserMaintenanceRecordVo> page = userMaintenanceRecordService
                .listUserMaintenanceRecords(userId, queryDto);
        log.info("User {} queried maintenance records page {}", userId, page);
        return ResultUtils.success(page.getRecords());
    }

    @GetMapping("/{id}")
    public BaseResponse<UserMaintenanceRecordVo> getRecordDetail(@PathVariable("id") Long id, HttpServletRequest request)
    {
        Long userId = Long.parseLong(request.getHeader("userId"));
        ThrowUtils.throwIf(userId == null, ErrorCode.NOT_LOGIN_ERROR, "login required");
        UserMaintenanceRecordVo record = userMaintenanceRecordService
                .getUserMaintenanceRecordDetail(userId, id);
        log.info("User {} fetched maintenance record detail {}", userId, id);
        return ResultUtils.success(record);
    }

    @PostMapping("/cancel")
    public BaseResponse<Boolean> cancelRecord(@RequestBody @Valid DeleteDto deleteDto, HttpServletRequest request)
    {
        Long userId = Long.parseLong(request.getHeader("userId"));
        ThrowUtils.throwIf(userId == null, ErrorCode.NOT_LOGIN_ERROR, "login required");
        Boolean result = userMaintenanceRecordService.cancelMaintenanceRecord(userId, deleteDto.getId());
        log.info("User {} cancelled maintenance record {}", userId, deleteDto.getId());
        return ResultUtils.success(result);
    }
}
