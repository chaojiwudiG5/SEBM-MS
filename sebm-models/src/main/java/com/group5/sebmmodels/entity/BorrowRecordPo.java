package com.group5.sebmmodels.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 设备借用记录表
 * @TableName borrowRecord
 */
@Data
@TableName("borrowRecord")
public class BorrowRecordPo {
    /**
     * 借用记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 借用人ID，关联user表
     */
    private Long userId;

    /**
     * 借用设备ID，关联device表
     */
    private Long deviceId;

    /**
     * 借出时间
     */
    private Date borrowTime;

    /**
     * 应还时间
     */
    private Date dueTime;

    /**
     * 实际归还时间，NULL表示未归还
     */
    private Date returnTime;

    /**
     * 状态 0 - 已借出 1 - 已归还 2 - 逾期
     */
    private Integer status;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;
}