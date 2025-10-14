package com.group5.borrow.controller;

import com.group5.sebm.common.service.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Borrow")
@RequestMapping("/api/borrow")
public class BorrowController {
  @DubboReference
  private IUserService userService;
}
