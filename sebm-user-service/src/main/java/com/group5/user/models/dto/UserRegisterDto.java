package com.group5.user.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDto {
  @NotBlank(message = "用户名不能为空")
  private String username;

  @NotBlank(message = "密码不能为空")
  @Size(min = 6, message = "密码至少6位")
  private String password;

  @NotBlank(message = "请确认密码")
  @Size(min = 6, message = "确认密码至少6位")
  private String checkPassword;
}
