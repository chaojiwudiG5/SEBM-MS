package com.group5.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.group5.sebm.common.common.BaseResponse;
import com.group5.sebm.common.common.ResultUtils;
import com.group5.sebm.common.models.dto.DeleteDto;
import com.group5.sebm.common.models.dto.PageDto;
import com.group5.user.models.dto.UserLoginDto;
import com.group5.user.models.dto.UserRegisterDto;
import com.group5.user.models.dto.UserUpdateDto;
import com.group5.user.models.po.UserPo;
import com.group5.user.models.vo.UserVo;
import com.group5.user.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "User")
@RequestMapping("/user")
public class UserController {

  private UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public BaseResponse<Long> userRegister(@RequestBody @Valid UserRegisterDto userRegisterDto) {
    Long userId = userService.userRegister(userRegisterDto);
    log.info("UserRegister called with userId: {}", userId);
    return ResultUtils.success(userId); // 返回ID
  }

  @PostMapping("/login")
  public BaseResponse<UserVo> userLogin(@RequestBody @Valid UserLoginDto UserLoginDto,
      HttpServletRequest request) {
    UserVo userVo = userService.userLogin(UserLoginDto, request);
    log.info("UserLogin called with userVo: {}", userVo);
    return ResultUtils.success(userVo); // 返回VO
  }

  @PostMapping("/logout")
  public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
    Boolean isLogout = userService.userLogout(request);
    log.info("UserLogout called with request: {}", request);
    return ResultUtils.success(isLogout); // 返回Boolean
  }

  //TODO只有管理员能查看所有用户，需编写AOP进行权限控制
  @PostMapping("/admin/getUserList")
  public BaseResponse<Page<UserVo>> getAllUsers(@RequestBody @Valid PageDto pageDto) {
    Page<UserVo> userVoPage = userService.getAllUsers(pageDto);
    log.info("GetAllUsers called with pageDto: {}, userVoPage: {}", pageDto, userVoPage);
    return ResultUtils.success(userVoPage); // 返回Page
  }

  @PostMapping("/admin/deleteUser")
  public BaseResponse<Boolean> deleteUser(@RequestBody @Valid DeleteDto deleteDto) {
    Boolean isDelete = userService.deleteUser(deleteDto);
    log.info("DeleteUser called with deleteDto: {}, isDelete: {}", deleteDto, isDelete);
    return ResultUtils.success(isDelete); // 返回Boolean
  }

  @PostMapping("/updateUser")
  public BaseResponse<UserVo> updateUser(@RequestBody @Valid UserUpdateDto userUpdateDto) {
      UserVo userVo = userService.updateUser(userUpdateDto);
      log.info("UpdateUser called with userUpdateDto: {}, userVo: {}", userUpdateDto, userVo);
      return ResultUtils.success(userVo); // 返回VO
  }

  @GetMapping("/getUserInfo/{id}")
  public BaseResponse<UserPo> getUserInfo(@PathVariable("id") Long id) {
    UserPo po = userService.getById(id);
    log.info("GetUserInfo called with id: {}, po: {}", id, po);
    return ResultUtils.success(po); // 返回PO
  }
}
