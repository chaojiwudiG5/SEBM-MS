package com.group5.sebmmodels.dto.borrow;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.Data;

@Data
public class BorrowRecordReturnDto {
  /**
   * 借用记录ID（必填，用于定位要更新的记录）
   */
  @NotNull(message = "BorrowRecord ID cannot be null")
  private Long id;
  /**
   * 归还时经纬度信息（必填）
   */
  @NotNull(message = "Latitude and longitude cannot be null")
  private String latitude;
  /**
   * 归还时经纬度信息（必填）
   */
  @NotNull(message = "Latitude and longitude cannot be null")
  private String longitude;
  /**
   * 实际归还时间（归还时填写）
   */
  @NotNull(message = "Return time cannot be null")
  private Date returnTime;
  /**
   * 备注（例如归还时填写损坏情况等）
   */
  private String remarks;
}
