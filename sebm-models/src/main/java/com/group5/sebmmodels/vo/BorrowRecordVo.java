package com.group5.sebmmodels.vo;

import java.util.Date;
import lombok.Data;

/**
 * 设备借用记录 VO，用于前端展示
 */
@Data
public class BorrowRecordVo {

  /**
   * 借用记录ID
   */
  private Long id;

  /**
   * 借用人ID
   */
  private Long userId;

  /**
   * 借用人姓名（可选，前端展示用）
   */
  private String userName;

  /**
   * 借用设备ID
   */
  private Long deviceId;

  /**
   * 设备名称（可选，前端展示用）
   */
  private String deviceName;
  /**
   * 设备图片（可选，前端展示用）
   */
  private String image;
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
