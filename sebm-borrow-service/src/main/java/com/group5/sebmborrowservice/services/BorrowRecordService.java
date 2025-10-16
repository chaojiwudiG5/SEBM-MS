package com.group5.sebmborrowservice.services;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.group5.sebmmodels.dto.borrow.BorrowRecordAddDto;
import com.group5.sebmmodels.dto.borrow.BorrowRecordDto;
import com.group5.sebmmodels.dto.borrow.BorrowRecordQueryDto;
import com.group5.sebmmodels.dto.borrow.BorrowRecordQueryWithStatusDto;
import com.group5.sebmmodels.dto.borrow.BorrowRecordReturnDto;
import com.group5.sebmmodels.entity.BorrowRecordPo;
import com.group5.sebmmodels.entity.DevicePo;
import com.group5.sebmmodels.vo.BorrowRecordVo;

/**
* @author Luoimo
* @description 针对表【borrowRecord(设备借用记录表)】的数据库操作Service
* @createDate 2025-09-26 11:27:18
*/
public interface BorrowRecordService extends IService<BorrowRecordPo> {
  BorrowRecordDto getBorrowRecordById(Long borrowRecordId);

  BorrowRecordVo borrowDevice(BorrowRecordAddDto borrowRecordAddDto, Long userId);

  Page<BorrowRecordVo> getBorrowRecordList(BorrowRecordQueryDto borrowRecordQueryDto);

  BorrowRecordVo returnDevice(BorrowRecordReturnDto borrowRecordReturnDto, Long userId);

  Page<BorrowRecordVo> getBorrowRecordListWithStatus(
      BorrowRecordQueryWithStatusDto borrowRecordQueryWithStatusDto);
}
