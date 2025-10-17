package com.group5.sebmmodels.dto.borrow;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BorrowRecordQueryWithStatusDto extends BorrowRecordQueryDto {
  @NotNull(message = "Status cannot be null")
  private Integer status;
}
