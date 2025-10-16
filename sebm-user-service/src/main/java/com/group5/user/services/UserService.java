package com.group5.user.services;

import com.baomidou.mybatisplus.extension.service.IService;
import com.group5.sebmmodels.dto.user.LoginDto;
import com.group5.sebmmodels.dto.user.RegisterDto;
import com.group5.sebmmodels.dto.user.UpdateDto;
import com.group5.sebmmodels.dto.user.UserDto;
import com.group5.sebmmodels.entity.UserPo;
import com.group5.sebmmodels.vo.UserVo;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Luoimo
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2025-09-16 13:06:33
 */
public interface UserService extends IService<UserPo> {

//  Page<UserVo> getAllUsers(PageDto pageDto);

//  UserVo getDiscountUserById(Long id);

  UserVo getCurrentUser(HttpServletRequest request);

  UserDto getCurrentUserDtoFromHttp(HttpServletRequest request);

  UserDto getCurrentUserDtoFromID(Long id);

  Long userRegister(RegisterDto registerDto);

  UserVo userLogin(LoginDto loginDto);

  UserVo updateUser(UpdateDto updateDto,HttpServletRequest request);
}
