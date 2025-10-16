package com.group5.sebmmodels.dto.maintenance;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户报修单查询请求
 */
@Data
public class UserQueryDto {

    /**
     * 页码
     */
    @NotNull(message = "pageNumber cannot be null")
    @Min(value = 1, message = "pageNumber must be greater than or equal to 1")
    private Integer pageNumber;

    /**
     * 每页数量
     */
    @NotNull(message = "pageSize cannot be null")
    @Min(value = 1, message = "pageSize must be greater than or equal to 1")
    private Integer pageSize;
    /**
     * 状态筛选
     */
    private Integer status;
}