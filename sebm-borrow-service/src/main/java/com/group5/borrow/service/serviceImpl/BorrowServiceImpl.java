package com.group5.borrow.service.serviceImpl;

import com.group5.sebm.common.models.vo.UserVo;
import com.group5.sebm.common.service.IUserService;
import com.group5.borrow.service.BorrowService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class BorrowServiceImpl implements BorrowService {
  @DubboReference
  private IUserService userService;

  UserVo getUserById(Long id) {
    return userService.getUserById(id);
  }
}
