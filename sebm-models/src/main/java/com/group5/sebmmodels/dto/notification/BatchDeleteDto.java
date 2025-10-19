package com.group5.sebmmodels.dto.notification;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批量删除 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchDeleteDto {
    
    /**
     * 要删除的ID列表
     */
    @NotEmpty(message = "ID list cannot be empty")
    private List<Long> ids;
}

