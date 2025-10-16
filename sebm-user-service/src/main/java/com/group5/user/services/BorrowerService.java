package com.group5.user.services;


import com.group5.sebmmodels.dto.common.DeleteDto;

/**
 * @author
 * @description 针对 Borrower 用户的额外服务接口
 */
public interface BorrowerService extends UserService {

    /**
     * 用户注销
     *
     * @param deleteDto 用户id
     * @return 是否删除成功
     */
    Boolean deactivateUser(DeleteDto deleteDto);

    Boolean updateBorrowedCount(Long userId, Integer borrowedCount);
}
