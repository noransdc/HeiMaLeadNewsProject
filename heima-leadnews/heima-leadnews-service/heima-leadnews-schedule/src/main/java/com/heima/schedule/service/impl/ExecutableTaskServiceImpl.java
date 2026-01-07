package com.heima.schedule.service.impl;


import com.heima.model.schedule.entity.ScheduleTask;
import com.heima.schedule.constant.TaskStatusEnum;
import com.heima.schedule.mapper.ScheduleTaskMapper;
import com.heima.schedule.service.ExecutableTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class ExecutableTaskServiceImpl implements ExecutableTaskService {

    @Autowired
    private ScheduleTaskMapper scheduleTaskMapper;

    @Transactional
    @Override
    public List<ScheduleTask> pickAuditExecutableTasks() {

        List<ScheduleTask> executableTasks = scheduleTaskMapper.pickAuditExecutableTasks(new Date());
        log.info("pickAuditExecutableTasks size:{}", executableTasks.size());
        for (ScheduleTask task : executableTasks) {
            log.info("task:{}", task);
        }

        if (CollectionUtils.isEmpty(executableTasks)){
            return executableTasks;
        }

        scheduleTaskMapper.markStatusBatch(executableTasks, TaskStatusEnum.RUNNING.getCode(),
                TaskStatusEnum.INIT.getCode(), TaskStatusEnum.FAIL.getCode());

        return executableTasks;
    }



}
