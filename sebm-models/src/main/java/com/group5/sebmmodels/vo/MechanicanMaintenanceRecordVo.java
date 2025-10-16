package com.group5.sebmmodels.vo;

import java.util.Date;
import lombok.Data;

/**
 * 技工维修单响应体
 */
@Data
public class MechanicanMaintenanceRecordVo {

    private Long id;

    private Long deviceId;

    private Long userId;

    private String description;

    private String image;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    private Long userMaintenanceRecordId;
}