package com.heima.schedule.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.apis.articlecore.ArticleCoreClient;
import com.heima.common.constants.ArticleTaskType;
import com.heima.model.articlecore.dto.ArticlePublishDto;
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
import org.springframework.util.CollectionUtils;

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

    @Scheduled(initialDelay = 20 * 1000, fixedDelay = 1 * 60 * 1000)
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

                String parameters = item.getParameters();
                ArticleParameterDto parameterDto;
                try {
                    parameterDto = JSON.parseObject(parameters, ArticleParameterDto.class);

                } catch (Exception e){
                    log.error("task {} parse json failed, parameters={}", item.getId(), parameters, e);
                    markFailUnrecoverable(item.getId(), "parse json error:" + item);
                    return;
                }

                if (parameterDto != null){
                    try {
                        articleCoreClient.postAudit(parameterDto.getArticleId());
                        log.info("articleCoreClient.postAudit success");
                        markSuccess(item.getId());

                    } catch (Exception e){
                        log.warn("task {} audit rpc failed", item.getId(), e);
                        markFailRetryable(item.getId(), "articleCoreClient.postAudit error:{}" + e.getMessage());
                    }
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

    private ScheduleTask buildTaskByAction(Long articleId, Date publishTime, String action){
        ScheduleTask task = new ScheduleTask();
        task.setTaskType(action);
        task.setBizKey(TaskUtil.getBizKey(action, articleId));
        task.setExecuteTime(publishTime);

        ArticleParameterDto parameterDto = new ArticleParameterDto(articleId, publishTime);

        task.setParameters(JSON.toJSONString(parameterDto));

        return task;
    }


}
