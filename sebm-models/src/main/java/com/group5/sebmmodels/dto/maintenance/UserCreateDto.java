package com.group5.sebmmodels.dto.maintenance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户报修单创建请求
 */
@Data
public class UserCreateDto {

    /**
     * 借用记录ID
     */
    @NotNull(message = "borrow record ID cannot be null")
    private Long borrowRecordId;

    /**
     * 故障描述
     */
    @NotBlank(message = "description cannot be blank")
    @Size(max = 500, message = "description cannot exceed 500 characters")
    private String description;

    /**
     * 故障图片
     */
    //image URL cannot be blank
    @NotBlank(message = "image URL cannot be blank")
    @Size(max = 1024, message = "image URL is too long")
    private String image;
}