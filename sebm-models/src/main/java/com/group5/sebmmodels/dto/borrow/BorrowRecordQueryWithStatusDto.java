package com.group5.sebmmodels.dto.borrow;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BorrowRecordQueryWithStatusDto extends BorrowRecordQueryDto {
  @NotNull(message = "Status cannot be null")
  private Integer status;
}
