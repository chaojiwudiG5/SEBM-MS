package com.group5.user.controller.inner;

import com.group5.sebmmodels.dto.user.UserDto;
import com.group5.sebmserviceclient.service.UserFeignClient;
import com.group5.user.services.BorrowerService;
import com.group5.user.services.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inner")
public class UserInnerController implements UserFeignClient {

  @Resource
  private UserService userService;
  @Resource
  private BorrowerService borrowerService;

  @Override
  public UserDto getCurrentUserDtoFromID(Long id) {
    return userService.getCurrentUserDtoFromID(id);
  }

  @Override
  public Boolean updateBorrowedCount(Long userId, Integer borrowedCount) {
    return borrowerService.updateBorrowedCount(userId, borrowedCount);
  }
}
