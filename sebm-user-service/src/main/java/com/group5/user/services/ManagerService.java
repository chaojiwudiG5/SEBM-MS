package com.group5.user.services;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.group5.sebmmodels.dto.common.DeleteDto;
import com.group5.sebmmodels.dto.common.PageDto;
import com.group5.sebmmodels.vo.UserVo;
import java.util.List;

/**
 * @author Deshperaydon
 * @date 2025/9/26
 */
public interface ManagerService {
    Boolean deleteBorrower(DeleteDto deleteDto);
    Boolean deleteBorrowers(List<Long> ids);
    Page<UserVo> getAllBorrowers(PageDto pageDto);
    Boolean updateBorrower(UserVo userVo);
}
