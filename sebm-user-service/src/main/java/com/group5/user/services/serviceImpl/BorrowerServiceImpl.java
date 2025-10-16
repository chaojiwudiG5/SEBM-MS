package com.group5.user.services.serviceImpl;

import com.group5.sebmcommon.exception.BusinessException;
import com.group5.sebmcommon.exception.ErrorCode;
import com.group5.sebmmodels.entity.UserPo;
import com.group5.user.services.BorrowerService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@NoArgsConstructor
public class BorrowerServiceImpl extends UserServiceImpl implements BorrowerService {

  @Override
  public Boolean updateBorrowedCount(Long userId, Integer borrowedCount) {
    //1. get user by id
    UserPo user = this.getById(userId);
    //2. update borrowedCount
    try {
      Integer borrowedDeviceCount = user.getBorrowedDeviceCount();
      user.setBorrowedDeviceCount(borrowedDeviceCount + borrowedCount);
      this.updateById(user);
    }catch (Exception e){
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Update borrowedCount failed");
    }
    return true;
  }
}