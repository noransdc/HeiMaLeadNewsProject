package com.heima.schedule.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.articlecore.dto.ArticlePublishDto;
import com.heima.model.schedule.entity.ScheduleTask;


public interface ScheduleTaskService extends IService<ScheduleTask> {


    void addScheduleTask(ArticlePublishDto dto);


}
