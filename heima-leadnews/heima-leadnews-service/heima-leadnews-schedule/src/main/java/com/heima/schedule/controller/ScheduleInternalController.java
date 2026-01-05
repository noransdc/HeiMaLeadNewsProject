package com.heima.schedule.controller;


import com.heima.model.articlecore.dto.ArticlePublishDto;
import com.heima.model.schedule.dto.ArticleTaskDto;
import com.heima.schedule.service.ScheduleTaskService;
import com.heima.schedule.service.TaskInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/internal/schedule")
public class ScheduleInternalController {

    @Autowired
    private ScheduleTaskService scheduleTaskService;

    @PostMapping("/tasks/add")
    void addScheduleTasks(@RequestBody List<ArticlePublishDto> list){
        scheduleTaskService.addTask(list);
    }

}
