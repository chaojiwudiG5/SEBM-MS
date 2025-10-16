package com.group5.sebmmaintenanceservice.service.services;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.group5.sebmmodels.dto.maintenance.MechanicRecordQueryDto;
import com.group5.sebmmodels.dto.maintenance.MechanicanQueryDto;
import com.group5.sebmmodels.dto.maintenance.MechanicanUpdateDto;
import com.group5.sebmmodels.entity.MechanicanMaintenanceRecordPo;
import com.group5.sebmmodels.vo.MechanicanMaintenanceRecordVo;

/**
* @author Luoimo
* @description 针对表【mechanicanMaintenanceRecord(技工设备维修报单表)】的数据库操作Service
* @createDate 2025-09-26 13:41:31
*/
public interface MechanicanMaintenanceRecordService extends IService<MechanicanMaintenanceRecordPo> {

    /**
     * 技工认领报修单生成维修任务
     */
    Long addMaintenanceTask(Long mechanicId, Long userMaintenanceRecordId);

    /**
     * 分页查询技工自己的维修任务
     */
    Page<MechanicanMaintenanceRecordVo> listMechanicMaintenanceRecords(Long mechanicId, MechanicanQueryDto queryDto);

    /**
     * 查看维修任务详情
     */
    MechanicanMaintenanceRecordVo getMechanicMaintenanceRecordDetail(MechanicRecordQueryDto queryDto);

    /**
     * 更新维修任务状态
     */
    Boolean updateMechanicMaintenanceRecord(Long mechanicId, MechanicanUpdateDto updateDto);
}