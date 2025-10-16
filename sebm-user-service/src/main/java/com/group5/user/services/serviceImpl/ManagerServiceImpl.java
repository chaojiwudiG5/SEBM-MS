package com.group5.user.services.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.group5.sebmcommon.exception.BusinessException;
import com.group5.sebmcommon.exception.ErrorCode;
import com.group5.sebmcommon.exception.ThrowUtils;
import com.group5.sebmmodels.dto.common.DeleteDto;
import com.group5.sebmmodels.dto.common.PageDto;
import com.group5.sebmmodels.entity.UserPo;
import com.group5.sebmmodels.vo.UserVo;
import com.group5.user.services.ManagerService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class ManagerServiceImpl extends UserServiceImpl implements ManagerService {
    /**
     * 删除用户
     *
     * @param deleteDto 用户id
     * @return 是否删除成功
     */
    public Boolean deleteBorrower(DeleteDto deleteDto) {
        //1. check if user exists
        UserPo userPo = baseMapper.selectById(deleteDto.getId());
        ThrowUtils.throwIf(userPo == null, ErrorCode.NOT_FOUND_ERROR, "User not exists");
        //2. delete user from database
        try {
            baseMapper.deleteById(deleteDto.getId());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Delete failed");
        }
        return true;
    }

    /**
     * 批量删除用户
     */
    public Boolean deleteBorrowers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id list is empty");
        }
        try {
            this.removeByIds(ids);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Batch delete failed");
        }
        return true;
    }

    /**
     * 获取所有用户
     *
     * @param pageDto 分页信息
     * @return 用户列表
     */
    public Page<UserVo> getAllBorrowers(PageDto pageDto) {
        // 1. 创建分页对象
        Page<UserPo> page = new Page<>(pageDto.getPageNumber(), pageDto.getPageSize());

        // 2. 执行分页查询
        Page<UserPo> userPage = baseMapper.selectPage(page, new QueryWrapper<>());

        // 3. 将 PO 转 VO
        List<UserVo> voList = userPage.getRecords().stream()
                .map(po -> {
                    UserVo vo = new UserVo();
                    BeanUtils.copyProperties(po, vo);
                    return vo;
                })
                .collect(Collectors.toList());


        // 4. 将 VO 列表放回 Page 对象
        Page<UserVo> resultPage = new Page<>(userPage.getCurrent(), userPage.getSize(),
                userPage.getTotal());
        resultPage.setRecords(voList);

        return resultPage;
    }
    /**
     * 修改用户
     *
     * @param userVo 修改后的用户信息
     * @return 是否修改成功
     */
    public Boolean updateBorrower(UserVo userVo) {
        if (userVo == null || userVo.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "User id is required");
        }

        // 1. 先查是否存在
        UserPo existingUser = baseMapper.selectById(userVo.getId());
        ThrowUtils.throwIf(existingUser == null, ErrorCode.NOT_FOUND_ERROR, "User not exists");

        // 2. 将 VO 转换成 PO（只覆盖要更新的字段）
        UserPo updateUser = new UserPo();
        BeanUtils.copyProperties(userVo, updateUser);

        // 3. 执行更新
        try {
            int rows = baseMapper.updateById(updateUser);
            if (rows <= 0) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Update failed");
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Update failed: " + e.getMessage());
        }

        return true;
    }


}
