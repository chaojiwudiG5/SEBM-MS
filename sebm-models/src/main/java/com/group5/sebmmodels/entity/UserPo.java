package com.group5.sebmmodels.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户表（大表方案，包含所有字段）
 */
@TableName(value = "user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPo {

  /** id */
  @TableId(type = IdType.AUTO)
  private Long id;

  /** 用户昵称 */
  private String username;

  /** 密码 */
  private String password;

  /** 邮箱 */
  private String email;

  /** 电话 */
  private String phone;

  /** 性别 */
  private Integer gender;

  /** 用户头像 */
  private String avatarUrl;

  /** 用户角色 0 - 普通用户 1 - 管理员 2 - 技工 */
  private Integer userRole;

  /** 是否删除（逻辑删除） */
  @TableLogic
  private Integer isDelete;

  /** 更新时间 */
  private Date updateTime;

  /** 创建时间 */
  private Date createTime;

  /** 状态 0 - 正常 */
  private Integer userStatus;

  /** 年龄 */
  private Integer age;

  /** 等级 */
  private Integer level;

  /** 逾期次数 */
  private Integer overdueTimes;

  /** 已借设备数 */
  private Integer borrowedDeviceCount;

  /** 最大可借设备数 */
  private Integer maxBorrowedDeviceCount;

  /** 最大逾期次数 */
  private Integer maxOverdueTimes;
}
