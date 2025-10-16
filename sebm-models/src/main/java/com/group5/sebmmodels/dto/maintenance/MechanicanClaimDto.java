package com.group5.sebmmodels.dto.maintenance;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 技工认领报修单请求
 */
@Data
public class MechanicanClaimDto {

    /**
     * 用户报修单ID
     */
    @NotNull(message = "userMaintenanceRecordId cannot be null")
    private Long userMaintenanceRecordId;
}