package com.group5.user.models.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户
 *
 * @TableName user
 */
@TableName(value = "user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPo {

  /**
   * id
   */
  @TableId(type = IdType.AUTO)
  private Long id;

  /**
   * 用户昵称
   */
  private String username;

  /**
   * 密码
   */
  private String password;

  /**
   * 邮箱
   */
  private String email;

  /**
   * 电话
   */
  private String phone;

  /**
   * 性别
   */
  private Integer gender;

  /**
   * 用户头像
   */
  private String avatarUrl;

  /**
   * 用户角色 0 - 普通用户 1 - 管理员
   */
  private Integer userRole;

  /**
   * 是否删除
   */
  private Integer isDelete;

  /**
   * 更新时间
   */
  private Date updateTime;

  /**
   * 创建时间
   */
  private Date createTime;

  /**
   * 状态 0 - 正常
   */
  private Integer userStatus;

  /**
   *
   */
  private Integer age;
}