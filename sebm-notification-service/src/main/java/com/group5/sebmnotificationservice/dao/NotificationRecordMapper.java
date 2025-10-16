package com.group5.sebmnotificationservice.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.group5.sebmmodels.entity.NotificationRecordPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知记录 Mapper
 */
@Mapper
public interface NotificationRecordMapper extends BaseMapper<NotificationRecordPo> {

}

