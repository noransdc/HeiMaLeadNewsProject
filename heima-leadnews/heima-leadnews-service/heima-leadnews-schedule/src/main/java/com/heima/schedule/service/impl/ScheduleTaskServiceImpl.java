package com.heima.schedule.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.apis.articlecore.ArticleCoreClient;
import com.heima.common.constants.ArticleTaskType;
import com.heima.model.articlecore.dto.ArticleTaskDto;
import com.heima.model.common.dtos.PageRequestDto;
import com.heima.model.schedule.dto.ArticleAuditCompensateDto;
import com.heima.model.schedule.dto.ArticleParameterDto;
import com.heima.model.schedule.entity.ScheduleTask;
import com.heima.schedule.constant.TaskStatusEnum;
import com.heima.schedule.mapper.ScheduleTaskMapper;
import com.heima.schedule.service.ExecutableTaskService;
import com.heima.schedule.service.ScheduleTaskService;
import com.heima.schedule.util.TaskUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class ScheduleTaskServiceImpl extends ServiceImpl<ScheduleTaskMapper, ScheduleTask> implements ScheduleTaskService {

    @Autowired
    private ArticleCoreClient articleCoreClient;

    @Autowired
    private ScheduleTaskMapper scheduleTaskMapper;

    @Autowired
    private ExecutableTaskService executableTaskService;

    @Autowired
    @Qualifier("scheduleTaskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;


    @Transactional
    @Override
    public void addScheduleTask(ArticleTaskDto dto) {
        if (dto == null || dto.getArticleId() == null || dto.getPublishTime() == null || StringUtils.isBlank(dto.getAction())){
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime publishTime = dto.getPublishTime();
        LocalDateTime executeTime = publishTime.isAfter(now) ? publishTime : now;

        ScheduleTask auditTask = buildTask(dto.getArticleId(), now, ArticleTaskType.ARTICLE_AUDIT);
        save(auditTask);

        ScheduleTask publishTask = buildTask(dto.getArticleId(), executeTime, ArticleTaskType.ARTICLE_PUBLISH);
        save(publishTask);

    }

    @Transactional
    @Scheduled(initialDelay = 30 * 1000, fixedDelay = 2 * 60 * 1000)
    public void compensateAuditTask(){
        PageRequestDto dto = new PageRequestDto();
        dto.setPage(1);
        dto.setSize(20);
        List<ArticleAuditCompensateDto> list = articleCoreClient.getArticleAuditCompensateList(dto);
        for (ArticleAuditCompensateDto item : list) {
            log.info("compensateAuditTask item:{}", item);
        }

        for (ArticleAuditCompensateDto item : list) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime publishTime = item.getPublishTime();
            LocalDateTime executeTime = publishTime.isAfter(now) ? publishTime : now;

            ScheduleTask auditTask = buildTask(item.getArticleId(), now, ArticleTaskType.ARTICLE_AUDIT);
            try {
                save(auditTask);

            } catch (DuplicateKeyException e){
                // 已存在，正常情况
            }

            ScheduleTask publishTask = buildTask(item.getArticleId(), executeTime, ArticleTaskType.ARTICLE_PUBLISH);
            try {
                save(publishTask);

            } catch (DuplicateKeyException e){
                // 已存在，正常情况
            }
        }

    }

    @Scheduled(initialDelay = 20 * 1000, fixedDelay = 2 * 60 * 1000)
    public void scanAuditExecutableTasks(){
        int active = taskExecutor.getActiveCount();
        int core = taskExecutor.getCorePoolSize();
        if (active >= core) {
            log.warn("executor busy: {}/{}", active, core);
            return;
        }

        List<ScheduleTask> tasks = executableTaskService.pickAuditExecutableTasks();
        if (CollectionUtils.isEmpty(tasks)){
            log.info("audit executable tasks is empty");
            return;
        }

        for (ScheduleTask item : tasks) {
            taskExecutor.execute(()->{

                String bizKey = item.getBizKey();
                String[] split = bizKey.split(":");
                Long articleId;
                try {
                    articleId = Long.parseLong(split[1]);

                } catch (Exception e){
                    log.error("task {} parse bizKey failed, bizKey={}", item.getId(), bizKey, e);
                    markFailUnrecoverable(item.getId(), "parse json error:" + item);
                    return;
                }

                try {
                    if (ArticleTaskType.ARTICLE_AUDIT.equals(item.getTaskType())){
                        articleCoreClient.postAudit(articleId);
                        log.info("articleCoreClient.postAudit success");
                        markSuccess(item.getId());

                    } else if (ArticleTaskType.ARTICLE_PUBLISH.equals(item.getTaskType())){
                        articleCoreClient.postPublish(articleId);
                        log.info("articleCoreClient.postPublish success");
                        markSuccess(item.getId());
                    }

                } catch (Exception e){
                    log.warn("task {} audit rpc failed", item.getId(), e);
                    markFailRetryable(item.getId(), "articleCoreClient.postAudit error:{}" + e.getMessage());
                }

            });
        }

    }

    private void markFailRetryable(Long taskId, String errorMsg){
        scheduleTaskMapper.markFailRetryable(taskId, TaskStatusEnum.FAIL.getCode(), TaskStatusEnum.RUNNING.getCode(), errorMsg);
    }

    private void markSuccess(Long taskId){
        scheduleTaskMapper.markSuccess(taskId, TaskStatusEnum.SUCCESS.getCode(), TaskStatusEnum.RUNNING.getCode());
    }

    private void markFailUnrecoverable(Long taskId, String errorMsg){
        scheduleTaskMapper.markFailUnrecoverable(taskId, TaskStatusEnum.FAIL.getCode(), TaskStatusEnum.RUNNING.getCode(), errorMsg);
    }

    private ScheduleTask buildTask(Long articleId, LocalDateTime executeTime, String action){
        ScheduleTask task = new ScheduleTask();
        task.setTaskType(action);
        task.setBizKey(TaskUtil.getBizKey(action, articleId));
        task.setExecuteTime(executeTime);

        ArticleParameterDto parameterDto = new ArticleParameterDto(articleId, action);

        task.setParameters(JSON.toJSONString(parameterDto));

        return task;
    }


}
