package com.group5.user.services.serviceImpl;

import com.group5.sebm.common.models.vo.UserVo;
import com.group5.sebm.common.service.IUserService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(interfaceClass = IUserService.class)
public class IUserServiceImpl implements IUserService {

  @Override
  public UserVo getUserById(Long id) {
    return null;
  }
}
