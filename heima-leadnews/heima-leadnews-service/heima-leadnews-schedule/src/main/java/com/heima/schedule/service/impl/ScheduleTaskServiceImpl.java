package com.heima.schedule.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.articlecore.dto.ArticlePublishDto;
import com.heima.model.schedule.entity.ScheduleTask;
import com.heima.schedule.mapper.ScheduleTaskMapper;
import com.heima.schedule.service.ScheduleTaskService;
import com.heima.schedule.util.TaskUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


@Service
public class ScheduleTaskServiceImpl extends ServiceImpl<ScheduleTaskMapper, ScheduleTask> implements ScheduleTaskService {

    @Autowired
    private ScheduleTaskMapper scheduleTaskMapper;

    @Override
    public void addTask(List<ArticlePublishDto> list) {
        if (CollectionUtils.isEmpty(list)){
            return;
        }

        List<ScheduleTask> taskList = new ArrayList<>();
        for (ArticlePublishDto dto : list) {
            if (dto.getArticleId() != null && dto.getPublishTime() != null && StringUtils.isNotBlank(dto.getAction())){
                taskList.add(convertToScheduleTask(dto));
            }
        }

        scheduleTaskMapper.insertBatch(taskList);

    }


    private ScheduleTask convertToScheduleTask(ArticlePublishDto dto){
        ScheduleTask task = new ScheduleTask();
        task.setTaskType(dto.getAction());
        task.setBizKey(TaskUtil.getBizKey(dto.getAction(), dto.getArticleId()));
        task.setExecuteTime(dto.getPublishTime());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("articleId", dto.getArticleId());
        jsonObject.put("publishTime", dto.getPublishTime());
        String json = jsonObject.toJSONString();

        task.setParameters(json);

        return task;
    }


}
