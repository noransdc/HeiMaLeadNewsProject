package com.heima.schedule.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.schedule.pojos.Task;
import com.heima.model.schedule.pojos.Taskinfo;


public interface TaskInfoService extends IService<Taskinfo> {


    long addTask(Task task);

    void finishTask(Long taskId);

    Task poll(Integer taskType, Integer priority);

}
