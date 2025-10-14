package com.group5.sebm.common.models.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageDto {

  /**
   * 页码，从1开始
   */
  @NotNull(message = "页码不能为空")
  @Min(value = 1, message = "页码必须大于等于1")
  private Integer pageNumber;

  /**
   * 每页条数
   */
  @NotNull(message = "每页条数不能为空")
  @Min(value = 1, message = "每页条数必须大于等于1")
  private Integer pageSize;
}
