package com.heima.schedule.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.apis.articlecore.ArticleCoreClient;
import com.heima.common.constants.ArticleTaskType;
import com.heima.model.articlecore.dto.ArticlePublishDto;
import com.heima.model.common.dtos.PageRequestDto;
import com.heima.model.schedule.dto.ArticleAuditCompensateDto;
import com.heima.model.schedule.entity.ScheduleTask;
import com.heima.schedule.mapper.ScheduleTaskMapper;
import com.heima.schedule.service.ScheduleTaskService;
import com.heima.schedule.util.TaskUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class ScheduleTaskServiceImpl extends ServiceImpl<ScheduleTaskMapper, ScheduleTask> implements ScheduleTaskService {

    @Autowired
    private ArticleCoreClient articleCoreClient;

    @Override
    public void addScheduleTask(ArticlePublishDto dto) {
        if (dto == null || dto.getArticleId() == null || dto.getPublishTime() == null || StringUtils.isBlank(dto.getAction())){
            return;
        }

        ScheduleTask task = buildTaskByAction(dto.getArticleId(), dto.getPublishTime(), dto.getAction());
        save(task);
    }

    @Scheduled(initialDelay = 60_000, fixedDelay = 300_000)
    public void compensateAuditTask(){
        PageRequestDto dto = new PageRequestDto();
        dto.setPage(1);
        dto.setSize(100);
        List<ArticleAuditCompensateDto> list = articleCoreClient.getArticleAuditCompensateList(dto);
        log.info("compensateAuditTask list:{}", list);

        for (ArticleAuditCompensateDto item : list) {
            ScheduleTask task = buildTaskByAction(item.getArticleId(), item.getPublishTime(), ArticleTaskType.ARTICLE_AUDIT);
            try {
                save(task);

            } catch (DuplicateKeyException e){
                // 已存在，正常情况
            } catch (Exception e){
                log.error("compensate audit task failed, articleId={}", item.getArticleId(), e);
            }
        }

    }

    private ScheduleTask buildTaskByAction(Long articleId, Date publishTime, String action){
        ScheduleTask task = new ScheduleTask();
        task.setTaskType(action);
        task.setBizKey(TaskUtil.getBizKey(action, articleId));
        task.setExecuteTime(publishTime);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("articleId", articleId);
        jsonObject.put("publishTime", publishTime);
        String json = jsonObject.toJSONString();

        task.setParameters(json);

        return task;
    }


}
