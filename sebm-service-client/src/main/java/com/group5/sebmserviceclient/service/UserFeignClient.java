package com.group5.sebmserviceclient.service;

import com.group5.sebmmodels.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Luoimo
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2025-09-16 13:06:33
 */
@FeignClient(name = "user-service", path = "/api/user/inner")
public interface UserFeignClient{
  @GetMapping("/getUser/id")
  UserDto getCurrentUserDtoFromID(@RequestParam("id") Long id);

  @PostMapping("/updateBorrowerCount")
  Boolean updateBorrowedCount(@RequestParam("userId") Long userId, @RequestParam("borrowedCount") Integer borrowedCount);
}
