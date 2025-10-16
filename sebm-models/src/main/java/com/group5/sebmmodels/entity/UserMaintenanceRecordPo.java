package com.group5.sebmmodels.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 设备维修报单表
 * @TableName userMaintenanceRecord
 */
@TableName(value ="userMaintenanceRecord")
@Data
public class UserMaintenanceRecordPo {
    /**
     * 维修单ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 设备ID，关联device表
     */
    private Long deviceId;

    /**
     * 报修人ID，关联user表
     */
    private Long userId;

    /**
     * 故障描述
     */
    private String description;

    /**
     * 故障图片
     */
    private String image;

    /**
     * 维修状态 0 - 处理中 1 - 已处理
     */
    private Integer status;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}