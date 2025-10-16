package com.group5.sebmmodels.dto.borrow;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.Data;

/**
 * 前端创建新的借用记录 DTO
 */
@Data
public class BorrowRecordAddDto {

    /**
     * 借用设备ID
     */
    @NotNull(message = "设备ID不能为空")
    private Long deviceId;

    /**
     * 借出时间
     */
    @NotNull(message = "借出时间不能为空")
    @FutureOrPresent(message = "借出时间必须是未来时间")
    private Date borrowTime;

    /**
     * 应还时间，必须在借出时间之后
     */
    @NotNull(message = "应还时间不能为空")
    @Future(message = "应还时间必须是未来时间")
    private Date dueTime;

    /**
     * 备注，可选
     */
    private String remarks;
}
