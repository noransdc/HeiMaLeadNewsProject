package com.heima.schedule.controller;


import com.heima.model.articlecore.dto.ArticlePublishDto;
import com.heima.schedule.service.ScheduleTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/internal/schedule")
public class ScheduleInternalController {

    @Autowired
    private ScheduleTaskService scheduleTaskService;

    @PostMapping("/tasks/add")
    void addScheduleTask(@RequestBody ArticlePublishDto dto){
        scheduleTaskService.addScheduleTask(dto);
    }

}
