package com.group5.sebmcommon.aop;


import com.group5.sebmcommon.annotation.AuthCheck;
import com.group5.sebmcommon.enums.UserRoleEnum;
import com.group5.sebmcommon.exception.BusinessException;
import com.group5.sebmcommon.exception.ErrorCode;
import com.group5.sebmcommon.exception.ThrowUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@AllArgsConstructor

public class AuthInterceptor {
  /**
   * 执行拦截
   *
   * @param joinPoint 切入点
   * @param authCheck 权限校验注解
   */
  @Around("@annotation(authCheck)")
  public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
    int mustRoleCode = authCheck.mustRole().getCode();
    RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
    HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

    // 当前登录用户
    Long userId = Long.parseLong(request.getHeader("userId"));
    Integer role = Integer.parseInt(request.getHeader("role"));


    // 不需要权限，放行
    if (mustRoleCode < 0) {
      return joinPoint.proceed();
    }

    // 当前用户角色
    UserRoleEnum userRoleEnum = UserRoleEnum.fromCode(role);

    // 没有权限，拒绝
    ThrowUtils.throwIf(userRoleEnum.getCode() != mustRoleCode, ErrorCode.NO_AUTH_ERROR);

    // 权限校验：必须管理员，但当前用户不是管理员
    if (mustRoleCode == UserRoleEnum.ADMIN.getCode() && userRoleEnum != UserRoleEnum.ADMIN) {
      throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    }

    // 通过权限校验，放行
    return joinPoint.proceed();
  }
}
