package com.group5.borrow.service.serviceImpl;

import com.group5.sebm.common.models.vo.UserVo;
import com.group5.sebm.common.service.IUserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BorrowServiceImplTest {
  @DubboReference(check = false)
  private IUserService userService;

  @Test
  void getUserById() {
    UserVo userVo = userService.getUserById(1L);
    System.out.println(userVo);
  }
}