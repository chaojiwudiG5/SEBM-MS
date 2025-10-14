package com.group5.user.services;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.group5.sebm.common.models.dto.DeleteDto;
import com.group5.sebm.common.models.dto.PageDto;
import com.group5.user.models.dto.UserLoginDto;
import com.group5.user.models.dto.UserRegisterDto;
import com.group5.user.models.dto.UserUpdateDto;
import com.group5.user.models.po.UserPo;
import com.group5.user.models.vo.UserVo;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Luoimo
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2025-09-16 13:06:33
 */
public interface UserService extends IService<UserPo> {

  Page<UserVo> getAllUsers(PageDto pageDto);

//  UserVo getDiscountUserById(Long id);

  UserVo getLoginUser(HttpServletRequest request);

  Boolean deleteUser(DeleteDto deleteDto);

  Long userRegister(UserRegisterDto userRegisterDto);

  UserVo userLogin(UserLoginDto userLoginDto,HttpServletRequest request);

  Boolean userLogout(HttpServletRequest request);

  UserVo updateUser(UserUpdateDto userUpdateDto);
}
