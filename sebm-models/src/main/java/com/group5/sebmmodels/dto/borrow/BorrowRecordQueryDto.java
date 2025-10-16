package com.group5.sebmmodels.dto.borrow;

import com.group5.sebmmodels.dto.common.PageDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 分页查询 BorrowRecord 的 DTO
 */
@Data
public class BorrowRecordQueryDto extends PageDto {

    /**
     * 借用人ID
     */
    @NotNull(message = "User ID cannot be null")
    private Long userId;
}
