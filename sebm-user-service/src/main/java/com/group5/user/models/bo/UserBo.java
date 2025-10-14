package com.group5.user.models.bo;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserBo {

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

  public boolean isokforDiscount() {
    return this.age <= 18;
  }

  public boolean validateTwicePassword(String password, String checkPassword) {
    Boolean isSame = password.equals(checkPassword);
    return isSame;
  }

  public boolean validatePassword(String input, String dbPassword, BCryptPasswordEncoder encoder) {
    Boolean isMatch = encoder.matches(input, dbPassword);
    return isMatch;
  }
}
