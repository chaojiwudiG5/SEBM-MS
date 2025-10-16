package com.group5.sebmmodels.dto.borrow;

import java.util.Date;
import lombok.Data;

@Data
public class BorrowRecordDto {

  /**
   * 借用记录ID
   */
  private Long id;

  /**
   * 借用人ID
   */
  private Long userId;

  /**
   * 借用设备ID
   */
  private Long deviceId;

  /**
   * 借出时间
   */
  private Date borrowTime;

  /**
   * 应还时间
   */
  private Date dueTime;

  /**
   * 实际归还时间，NULL表示未归还
   */
  private Date returnTime;

  /**
   * 状态 0 - 已借出 1 - 已归还 2 - 逾期
   */
  private Integer status;

  /**
   * 备注
   */
  private String remarks;
}
