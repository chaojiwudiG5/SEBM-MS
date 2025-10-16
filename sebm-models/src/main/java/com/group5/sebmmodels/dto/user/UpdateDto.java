package com.group5.sebmmodels.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户更新 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDto {

  /**
   * 用户ID（要更新谁）
   */
  @NotNull(message = "用户ID不能为空")
  private Long id;

  /**
   * 用户昵称
   */
  @NotBlank(message = "用户名不能为空")
  @Size(max = 50, message = "用户名长度不能超过50个字符")
  private String username;

  /**
   * 邮箱
   */
  @Email(message = "邮箱格式不正确")
  private String email;

  /**
   * 电话
   */
  @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
  private String phone;

  /**
   * 性别 0 = 未知，1 = 男，2 = 女
   */
  @NotNull(message = "性别不能为空")
  @Min(value = 0, message = "性别最小为0")
  @Max(value = 2, message = "性别最大为2")
  private Integer gender;

  /**
   * 年龄
   */
  @Min(value = 0, message = "年龄不能小于0")
  private Integer age;


}
