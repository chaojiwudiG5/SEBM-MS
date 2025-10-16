package com.group5.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.group5.sebmcommon.BaseResponse;
import com.group5.sebmcommon.ResultUtils;
import com.group5.sebmcommon.annotation.AuthCheck;
import com.group5.sebmcommon.utils.UserContext;
import com.group5.sebmmodels.dto.common.DeleteDto;
import com.group5.sebmmodels.dto.common.PageDto;
import com.group5.sebmmodels.dto.user.LoginDto;
import com.group5.sebmmodels.dto.user.RegisterDto;
import com.group5.sebmmodels.dto.user.UpdateDto;
import com.group5.sebmmodels.dto.user.UserDto;
import com.group5.sebmcommon.enums.UserRoleEnum;
import com.group5.sebmmodels.vo.UserVo;
import com.group5.user.services.BorrowerService;
import com.group5.user.services.ManagerService;
import com.group5.user.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@Tag(name = "User")
@RequestMapping("/")
@AllArgsConstructor
public class UserController {

  private final BorrowerService borrowerService;
  private final ManagerService managerService;
  private final UserService userServiceImpl;

  @PostMapping("/register")
  public BaseResponse<Long> userRegister(@RequestBody @Valid RegisterDto registerDto) {
    Long userId = borrowerService.userRegister(registerDto);
    log.info("UserRegister called with userId: {}", userId);
    return ResultUtils.success(userId); // 返回ID
  }

  @PostMapping("/login")
  public BaseResponse<UserVo> userLogin(@RequestBody @Valid LoginDto LoginDto) {
    UserVo userVo = borrowerService.userLogin(LoginDto);
    log.info("UserLogin called with userVo: {}", userVo);
    return ResultUtils.success(userVo); // 返回VO
  }


  @PostMapping("/admin/getUserList")
  @AuthCheck(mustRole = UserRoleEnum.ADMIN)
  public BaseResponse<Page<UserVo>> getAllUsers(@RequestBody @Valid PageDto pageDto) {
    Page<UserVo> userVoPage = this.managerService.getAllBorrowers(pageDto);
    log.info("GetAllUsers called with pageDto: {}, userVoPage: {}", pageDto, userVoPage);
    return ResultUtils.success(userVoPage); // 返回Page
  }

  @PostMapping("/admin/deleteUser")
  @AuthCheck(mustRole = UserRoleEnum.ADMIN)
  public BaseResponse<Boolean> deleteUser(@RequestBody @Valid DeleteDto deleteDto) {
    Boolean isDelete = this.managerService.deleteBorrower(deleteDto);
    log.info("DeleteUser called with deleteDto: {}, isDelete: {}", deleteDto, isDelete);
    return ResultUtils.success(isDelete); // 返回Boolean
  }

  @PostMapping("/admin/updateUser")
  @AuthCheck(mustRole = UserRoleEnum.ADMIN)
  public BaseResponse<Boolean> updateUser(@RequestBody @Valid UserVo userVo) {
    Boolean isUpdate = managerService.updateBorrower(userVo);
    log.info("UpdateUser called with userVo: {}, result: {}", userVo, isUpdate);
    return ResultUtils.success(isUpdate); // 返回Boolean
  }




  @PostMapping("/deactivateUser")
  public BaseResponse<Boolean> deactivateUser(@RequestBody @Valid DeleteDto deactivateUser) {
    Boolean isDeactivate = borrowerService.deactivateUser(deactivateUser);
    log.info("DeactivateUser called with userVo: {}, isDeactivate: {}", deactivateUser, isDeactivate);
    return ResultUtils.success(isDeactivate); // 返回Boolean
  }



  @PostMapping("/updateUser")
  public BaseResponse<UserVo> updateUser(@RequestBody @Valid UpdateDto updateDto,
      HttpServletRequest request) {

      UserVo userVo = borrowerService.updateUser(updateDto,request);
      log.info("UpdateUser called with userUpdateDto: {}, userVo: {}", updateDto, userVo);
      return ResultUtils.success(userVo); // 返回VO
  }

  @GetMapping("/getCurrentUser")
  public BaseResponse<UserVo> getCurrentUser(HttpServletRequest request) {
    UserVo currentUser = userServiceImpl.getCurrentUser(request);
    log.info("GetCurrentUser called with currentUser: {}", currentUser);
    return ResultUtils.success(currentUser); // 返回VO
  }

  @GetMapping("/getCurrentUserDto")
  public UserDto getCurrentUserDto() {
    Long userId = UserContext.getUserId();
    UserDto userDto = userServiceImpl.getCurrentUserDtoFromID(userId);
    log.info("GetCurrentUserDto called with userDto: {}", userDto);
    return userDto;
  }

  @GetMapping("/test")
  public BaseResponse<String> test() {
    String test = "test";
    log.info("Test called with test: {}", test);
    return ResultUtils.success(test); // 返回String
  }
}
