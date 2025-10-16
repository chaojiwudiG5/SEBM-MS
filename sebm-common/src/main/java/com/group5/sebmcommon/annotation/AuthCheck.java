package com.group5.sebmcommon.annotation;

import com.group5.sebmcommon.enums.UserRoleEnum;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

  /**
   * 必须有某个角色
   */
  UserRoleEnum mustRole() default UserRoleEnum.USER;
}
