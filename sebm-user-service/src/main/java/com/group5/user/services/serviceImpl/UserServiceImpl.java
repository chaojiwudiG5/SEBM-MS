package com.group5.user.services.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.group5.sebmcommon.exception.BusinessException;
import com.group5.sebmcommon.exception.ErrorCode;
import com.group5.sebmcommon.exception.ThrowUtils;
import com.group5.sebmcommon.utils.JwtUtils;
import com.group5.sebmmodels.bo.Borrower;
import com.group5.sebmmodels.dto.common.DeleteDto;
import com.group5.sebmmodels.dto.user.LoginDto;
import com.group5.sebmmodels.dto.user.RegisterDto;
import com.group5.sebmmodels.dto.user.UpdateDto;
import com.group5.sebmmodels.dto.user.UserDto;
import com.group5.sebmmodels.entity.UserPo;
import com.group5.sebmmodels.vo.UserVo;
import com.group5.user.dao.UserMapper;
import com.group5.user.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


/**
 * @author
 * @description 用户服务实现
 */
@Primary
@AllArgsConstructor
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserPo> implements UserService {

  protected final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  /**
   * 获取当前登录用户
   *
   * @param request http 请求
   * @return 当前登录用户
   */
  @Override
  public UserVo getCurrentUser(HttpServletRequest request) {
    Long userId = (Long) request.getAttribute("userId");
    if (userId == null) {
      ThrowUtils.throwIf(true, ErrorCode.NOT_LOGIN_ERROR, "Not login");
    }
    UserPo userPo = baseMapper.selectById(userId);
    UserVo userVo = new UserVo();
    BeanUtils.copyProperties(userPo, userVo);
    return userVo;
  }
  /**
   * 获取当前登录用户
   *
   * @param request http 请求
   * @return 当前登录用户
   */
  @Override
  public UserDto getCurrentUserDtoFromHttp(HttpServletRequest request) {
    Long userId = (Long) request.getAttribute("userId");
    if (userId == null) {
      ThrowUtils.throwIf(true, ErrorCode.NOT_LOGIN_ERROR, "Not login");
    }
    UserPo userPo = baseMapper.selectById(userId);
    UserDto userDto = new UserDto();
    BeanUtils.copyProperties(userPo, userDto);
    return userDto;
  }
    /**
     * 根据用户id获取用户信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    @Override
    public UserDto getCurrentUserDtoFromID(Long userId) {
      if (userId == null) {
        ThrowUtils.throwIf(true, ErrorCode.NOT_LOGIN_ERROR, "Not login");
      }

      UserPo userPo = baseMapper.selectById(userId);
      // 数据库没查到用户
      if (userPo == null) {
        ThrowUtils.throwIf(true, ErrorCode.NOT_FOUND_ERROR, "User not found");
      }
      UserDto userDto = new UserDto();
      BeanUtils.copyProperties(userPo, userDto);
      return userDto;
    }



  /**
   * 注册用户
   *
   * @param registerDto 用户信息
   * @return 用户id
   */
  @Override
  public Long userRegister(RegisterDto registerDto) {
    //1. check if user already exists
    UserPo userPo = baseMapper.selectOne(
        new QueryWrapper<UserPo>().eq("phone", registerDto.getPhone()));
    ThrowUtils.throwIf(userPo != null, ErrorCode.NOT_FOUND_ERROR, "User already exists");

    //2. check if checkPassword equals password
    Borrower borrower = new Borrower();
    BeanUtils.copyProperties(registerDto, borrower);
    boolean isPasswordSame = borrower.validateTwicePassword(registerDto.getPassword(),
        registerDto.getCheckPassword());
    ThrowUtils.throwIf(!isPasswordSame, ErrorCode.PARAMS_ERROR, "Passwords do not match");

    //3. create user
    UserPo po = new UserPo();
    BeanUtils.copyProperties(borrower, po);
    po.setBorrowedDeviceCount(0);
    po.setMaxBorrowedDeviceCount(3);
    po.setMaxOverdueTimes(3);
    po.setOverdueTimes(0);
    po.setPassword(passwordEncoder.encode(registerDto.getPassword()));

    //4. insert user into database
    baseMapper.insert(po);
    //5. return user id
    return po.getId();
  }

  @Override
  public UserVo userLogin(LoginDto loginDto) {
    // 1. 检查用户是否存在
    UserPo userPo = baseMapper.selectOne(
        new QueryWrapper<UserPo>().eq("username", loginDto.getUsername()));
    ThrowUtils.throwIf(userPo == null, ErrorCode.NOT_FOUND_ERROR, "Username not exists");

    // 2. 校验密码
    Borrower borrower = new Borrower();
    BeanUtils.copyProperties(userPo, borrower);
    boolean isPasswordCorrect = borrower.validatePassword(
        loginDto.getPassword(),
        userPo.getPassword(),
        passwordEncoder
    );
    ThrowUtils.throwIf(!isPasswordCorrect, ErrorCode.PASS_ERROR, "Password is incorrect");

    // 3. 生成 JWT token
    String token = JwtUtils.generateToken(userPo.getId(),userPo.getUserRole());

    // 4. 封装返回对象
    UserVo userVo = new UserVo();
    BeanUtils.copyProperties(userPo, userVo);
    userVo.setToken(token);
    return userVo;
  }


  /**
   * 更新用户信息
   *
   * @param updateDto 用户信息
   * @return 更新后的用户信息
   */
  @Override
  public UserVo updateUser(UpdateDto updateDto,HttpServletRequest request) {
    //0.check if it is the current login user
    Long userId = (Long) request.getAttribute("userId");
    ThrowUtils.throwIf(!Objects.equals(updateDto.getId(), userId), ErrorCode.NOT_LOGIN_ERROR, "Not login");
    //1. check if user exists
    UserPo userPo = baseMapper.selectById(updateDto.getId());
    ThrowUtils.throwIf(userPo == null, ErrorCode.NOT_FOUND_ERROR, "User not exists");
    //2. update user information
    UserPo newUserPo = new UserPo();
    BeanUtils.copyProperties(updateDto, newUserPo);
    baseMapper.updateById(newUserPo);
    //3. return updated user information
    UserVo userVo = new UserVo();
    BeanUtils.copyProperties(newUserPo, userVo);
    //4. return updated user information
    return userVo;
  }
  /**
   * 用户注销
   *
   * @param deleteDto 用户id
   * @return 是否删除成功
   */
  public Boolean deactivateUser(DeleteDto deleteDto) {
    // 1. 检查用户是否存在
    UserPo userPo = baseMapper.selectById(deleteDto.getId());
    ThrowUtils.throwIf(userPo == null, ErrorCode.NOT_FOUND_ERROR, "User not exists");

    // 2. 修改用户状态为“已注销”，假设 status=0 表示正常，status=1 表示注销
    userPo.setIsDelete(1);
    try {
      baseMapper.updateById(userPo);
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Deactivate failed");
    }

    return true;
  }


  @Override
  public boolean updateBatchById(Collection<UserPo> entityList, int batchSize) {
    return false;
  }

  @Override
  public boolean saveOrUpdate(UserPo entity) {
    return false;
  }

  @Override
  public UserPo getOne(Wrapper<UserPo> queryWrapper, boolean throwEx) {
    return null;
  }

  @Override
  public Optional<UserPo> getOneOpt(Wrapper<UserPo> queryWrapper, boolean throwEx) {
    return Optional.empty();
  }

  @Override
  public Map<String, Object> getMap(Wrapper<UserPo> queryWrapper) {
    return Map.of();
  }

  @Override
  public <V> V getObj(Wrapper<UserPo> queryWrapper, Function<? super Object, V> mapper) {
    return null;
  }

  @Override
  public Class<UserPo> getEntityClass() {
    return null;
  }
}
