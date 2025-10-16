package com.group5.sebmdeviceservice.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.group5.sebmmodels.entity.DevicePo;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Luoimo
* @description 针对表【device(设备表)】的数据库操作Mapper
* @createDate 2025-09-26 11:29:28
* @Entity generator.domain.Device
*/
@Mapper
public interface DeviceMapper extends BaseMapper<DevicePo> {

}




