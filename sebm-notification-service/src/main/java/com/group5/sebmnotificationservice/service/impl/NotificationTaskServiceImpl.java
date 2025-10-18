package com.group5.sebmnotificationservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.group5.sebmnotificationservice.dao.NotificationTaskMapper;
import com.group5.sebmmodels.entity.NotificationTaskPo;
import com.group5.sebmnotificationservice.service.NotificationTaskService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 通知任务服务实现类
 */
@Slf4j
@Service
@AllArgsConstructor
public class NotificationTaskServiceImpl extends ServiceImpl<NotificationTaskMapper, NotificationTaskPo>
        implements NotificationTaskService {

    
    @Override
    public Long createTask(String title, String content, Integer notificationRole) {
        try {
            // 如果 content 为 null，使用空字符串
            if (content == null) {
                content = "";
            }
            
            NotificationTaskPo task = NotificationTaskPo.builder()
                    .title(title)
                    .content(content)
                    .notificationRole(notificationRole)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            
            boolean result = this.save(task);
            
            if (result) {
                log.info("创建通知任务成功: taskId={}, title={}", task.getId(), title);
                return task.getId();
            } else {
                log.error("创建通知任务失败: title={}", title);
                return null;
            }
        } catch (Exception e) {
            log.error("创建通知任务时发生异常: title={}, error={}", title, e.getMessage(), e);
            return null;
        }
    }

}

