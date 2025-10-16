package com.group5.sebmborrowservice.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.group5.sebmmodels.entity.BorrowRecordPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
* @author Luoimo
* @description 针对表【borrowRecord(设备借用记录表)】的数据库操作Mapper
* @createDate 2025-09-26 11:27:18
* @Entity generator.domain.BorrowRecord
*/
@Mapper
public interface BorrowRecordMapper extends BaseMapper<BorrowRecordPo> {
  /**
   * 将借出且未归还且 overdue 的记录更新状态为 逾期
   */
  @Update("UPDATE borrowRecord " +
      "SET status = 2 " +
      "WHERE returnTime IS NULL " +
      "AND dueTime < NOW() " +
      "AND status <> 2")
  int updateStatusToOverdue();
}




