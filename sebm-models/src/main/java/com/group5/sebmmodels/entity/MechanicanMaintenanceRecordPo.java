package com.group5.sebmmodels.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 技工设备维修报单表
 * @TableName mechanicanMaintenanceRecord
 */
@TableName(value ="mechanicanMaintenanceRecord")
@Data
public class MechanicanMaintenanceRecordPo {
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
     * 维修人ID，关联user表
     */
    private Long userId;

    /**
     * 维修描述
     */
    private String description;

    /**
     * 维修完成图片
     */
    private String image;

    /**
     * 维修状态 0 - 待处理 1 - 处理中 2 - 已修复 3 - 无法修复
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
    /**
     * 维修记录ID，关联userMaintenanceRecord表
     */
    private Long userMaintenanceRecordId;
}