package com.group5.sebmborrowservice.controller.inner;

import com.group5.sebmborrowservice.services.BorrowRecordService;
import com.group5.sebmmodels.dto.borrow.BorrowRecordDto;
import com.group5.sebmserviceclient.service.BorrowRecordFeignClient;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inner")
public class BorrowRecordInnerController implements BorrowRecordFeignClient {

  @Resource
  private BorrowRecordService borrowRecordService;
  @Override
  public BorrowRecordDto getBorrowRecordById(Long borrowRecordId) {
    return borrowRecordService.getBorrowRecordById(borrowRecordId);
  }
}
