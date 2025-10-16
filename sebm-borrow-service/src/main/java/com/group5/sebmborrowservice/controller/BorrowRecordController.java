package com.group5.sebmborrowservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.group5.sebmborrowservice.services.BorrowRecordService;
import com.group5.sebmcommon.BaseResponse;
import com.group5.sebmcommon.ResultUtils;
import com.group5.sebmmodels.dto.borrow.BorrowRecordAddDto;
import com.group5.sebmmodels.dto.borrow.BorrowRecordQueryDto;
import com.group5.sebmmodels.dto.borrow.BorrowRecordQueryWithStatusDto;
import com.group5.sebmmodels.dto.borrow.BorrowRecordReturnDto;
import com.group5.sebmmodels.entity.DevicePo;
import com.group5.sebmmodels.vo.BorrowRecordVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Borrow")
@RequestMapping("/")
@AllArgsConstructor
public class BorrowRecordController {

  @Resource
  private BorrowRecordService borrowRecordService;

  @PostMapping("/borrowDevice")
  public BaseResponse<BorrowRecordVo> borrowDevice(
      @RequestBody BorrowRecordAddDto borrowRecordAddDto,
      HttpServletRequest request) {
    BorrowRecordVo borrowRecordVo = borrowRecordService.borrowDevice(borrowRecordAddDto,
        Long.parseLong(request.getHeader("userId")));
    log.info("addBorrowRecord success, borrowRecordVo: {}", borrowRecordVo);
    return ResultUtils.success(borrowRecordVo);
  }

  @PostMapping("/returnDevice")
  public BaseResponse<BorrowRecordVo> returnDevice(
      @RequestBody BorrowRecordReturnDto borrowRecordReturnDto,
      HttpServletRequest request) {
    BorrowRecordVo borrowRecordVo = borrowRecordService.returnDevice(borrowRecordReturnDto,
        Long.parseLong(request.getHeader("userId")));
    log.info("returnDevice success, borrowRecordVo: {}", borrowRecordVo);
    return ResultUtils.success(borrowRecordVo);
  }

  @PostMapping("/getBorrowRecordList")
  public BaseResponse<List<BorrowRecordVo>> getBorrowRecordList(
      @RequestBody BorrowRecordQueryDto borrowRecordQueryDto) {
    Page<BorrowRecordVo> borrowRecordPage = borrowRecordService.getBorrowRecordList(
        borrowRecordQueryDto);
    log.info("getMyBorrowRecordList called with borrowRecordQueryDto: {}, borrowRecordPage: {}",
        borrowRecordQueryDto, borrowRecordPage);
    return ResultUtils.success(borrowRecordPage.getRecords());
  }

  @PostMapping("/getBorrowRecordListWithStatus")
  public BaseResponse<List<BorrowRecordVo>> getBorrowRecordListWithStatus(
      @RequestBody BorrowRecordQueryWithStatusDto borrowRecordQueryWithStatusDto) {
    Page<BorrowRecordVo> borrowRecordPage = borrowRecordService.getBorrowRecordListWithStatus(
        borrowRecordQueryWithStatusDto);
    log.info(
        "getBorrowRecordListWithStatus called with borrowRecordQueryWithStatusDto: {}, borrowRecordPage: {}",
        borrowRecordQueryWithStatusDto, borrowRecordPage);
    return ResultUtils.success(borrowRecordPage.getRecords());
  }

}