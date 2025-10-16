package com.group5.sebmmodels.vo;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVo {

  private Long id;
  private String username;
  private String email;
  private String phone;
  private Integer gender;
  private String avatarUrl;
  private Integer userRole;
  private Integer userStatus;

  private Integer age;
  private Integer level;
  private Integer overdueTimes;
  private Integer borrowedDeviceCount;
  private Integer maxBorrowedDeviceCount;
  private Integer maxOverdueTimes;

  private Date createTime;
  private Date updateTime;

  /** 可由 Service 层设置 */
  private boolean isActive;
  private String token;
}
