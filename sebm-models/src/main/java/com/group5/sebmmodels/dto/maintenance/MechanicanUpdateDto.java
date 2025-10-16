package com.group5.sebmmodels.dto.maintenance;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 技工更新维修单状态
 */
@Data
public class MechanicanUpdateDto {

    /**
     * 技工维修单ID
     */
    @NotNull(message = "technicianMaintenanceRecordId cannot be null")
    private Long id;

    /**
     * 新状态：0-待处理，1-处理中，2-已修复，3-无法修复
     */
    @NotNull(message = "status cannot be null")
    @Min(value = 2, message = "illegal status value")
    @Max(value = 3, message = "illegal status value")
    private Integer status;

    /**
     * 维修描述
     */
    @Size(max = 500, message = "description cannot exceed 500 characters")
    private String description;

    /**
     * 完成图片
     */
    @Size(max = 1024, message = "image URL is too long")
    private String image;

    /**
     * 关联的用户报修单ID，可用于同步状态
     */
    @NotNull(message = "userMaintenanceRecordId cannot be null")
    private Long userMaintenanceRecordId;
}