package com.group5.sebmserviceclient.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.group5.sebmmodels.dto.borrow.BorrowRecordDto;
import com.group5.sebmmodels.entity.BorrowRecordPo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
* @author Luoimo
* @description 针对表【borrowRecord(设备借用记录表)】的数据库操作Service
* @createDate 2025-09-26 11:27:18
*/
@FeignClient(name = "sebm-borrow-service", path = "/api/borrow/inner")
public interface BorrowRecordFeignClient{
  @GetMapping("/getBorrowRecord/id")
  BorrowRecordDto getBorrowRecordById(@RequestParam("borrowRecordId") Long borrowRecordId);

}
