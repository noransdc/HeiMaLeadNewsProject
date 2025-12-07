package com.heima.schedule.feign;

import com.heima.apis.schedule.IScheduleClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.schedule.pojos.Task;
import com.heima.schedule.service.TaskInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
public class ScheduleClient implements IScheduleClient {

    @Autowired
    private TaskInfoService taskInfoService;



    @Override
    @PostMapping("/api/v1/schedule/add")
    public ResponseResult addTask(@RequestBody Task task){
        log.info("ScheduleClient addTask:{}", task);
        long taskId = taskInfoService.addTask(task);
        return ResponseResult.okResult(taskId);
    }

    @Override
    @GetMapping("/api/vi/schedule/task/{type}/{priority}")
    public ResponseResult<Task> pollTask(@PathVariable Integer type, @PathVariable Integer priority) {
        Task task = taskInfoService.poll(type, priority);
        return ResponseResult.okResult(task);
    }
}
