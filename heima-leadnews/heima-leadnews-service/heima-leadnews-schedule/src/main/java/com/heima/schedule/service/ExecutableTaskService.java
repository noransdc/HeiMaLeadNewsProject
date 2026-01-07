package com.heima.schedule.service;


import com.heima.model.schedule.entity.ScheduleTask;

import java.util.List;

public interface ExecutableTaskService {


    List<ScheduleTask> pickAuditExecutableTasks();

}
