package com.heima.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.ScheduleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.schedule.pojos.Task;
import com.heima.model.schedule.pojos.Taskinfo;
import com.heima.model.schedule.pojos.TaskInfoLog;
import com.heima.schedule.mapper.TaskInfoLogsMapper;
import com.heima.schedule.mapper.TaskInfoMapper;
import com.heima.schedule.service.TaskInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@Transactional
public class TaskInfoServiceImpl extends ServiceImpl<TaskInfoMapper, Taskinfo> implements TaskInfoService{



    @Autowired
    private CacheService cacheService;

    @Autowired
    private TaskInfoLogsMapper taskInfoLogsMapper;


    @Override
    public long addTask(Task task) {
        boolean result = addToDb(task);
        if (result){
            addToCache(task);
        }
        return task.getTaskId();
    }

    @Override
    public void finishTask(Long taskId) {
//        taskInfoMapper.deleteById(taskId);

        updateDb(taskId, ScheduleConstants.SUCCESS);
    }

    @Override
    public Task poll(Integer taskType, Integer priority) {
        String key = ScheduleConstants.TOPIC + taskType + "_" + priority;
        String json = cacheService.lRightPop(key);
        if (StringUtils.isBlank(json)){
            return null;
        }
        Task task = JSON.parseObject(json, Task.class);
        if (task == null){
            return null;
        }
        updateDb(task.getTaskId(), ScheduleConstants.RUNNING);

        return task;
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void refreshFutureTask(){
        log.info("refreshFutureTask");
        Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");

        for (String futureKey : futureKeys) {
            Set<String> taskJsons = cacheService.zRangeByScore(futureKey, 0, System.currentTimeMillis());

            if (!taskJsons.isEmpty()){
                String topicKey = ScheduleConstants.TOPIC + futureKey.replace(ScheduleConstants.FUTURE, "");
                cacheService.refreshWithPipeline(futureKey, topicKey, taskJsons);
            }
        }

    }

//    @Scheduled(cron = "0/30 * * * * ?")
    @Scheduled(cron = "0 */2 * * * ?")
    public void refreshDbTask(){

        Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");
        Set<String> topicKeys = cacheService.scan(ScheduleConstants.TOPIC + "*");
        cacheService.delete(futureKeys);
        cacheService.delete(topicKeys);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);

        LambdaQueryWrapper<TaskInfoLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(TaskInfoLog::getExecuteTime, calendar.getTime());
        wrapper.eq(TaskInfoLog::getStatus, ScheduleConstants.PENDING);
        List<TaskInfoLog> logs = taskInfoLogsMapper.selectList(wrapper);
        log.info("refreshDbTask, logs:{}", logs);
        if (logs == null || logs.isEmpty()){
            return;
        }
        for (TaskInfoLog log : logs) {
            Task task = new Task();
            BeanUtils.copyProperties(log, task);
            task.setExecuteTime(log.getExecuteTime().getTime());
            addToCache(task);
        }
    }


    private void updateDb(Long taskId, Integer status){
        TaskInfoLog logs = taskInfoLogsMapper.selectById(taskId);
        if (logs != null){
            logs.setStatus(status);
            taskInfoLogsMapper.updateById(logs);
        }

    }


    private boolean addToDb(Task task){
        try {
//            Taskinfo taskinfo = new Taskinfo();
//            BeanUtils.copyProperties(task, taskinfo);
//            taskinfo.setExecuteTime(new Date(task.getExecuteTime()));
//            taskInfoMapper.insert(taskinfo);
//
//            task.setTaskId(taskinfo.getTaskId());

            TaskInfoLog logs = new TaskInfoLog();
            BeanUtils.copyProperties(task, logs);
//            logs.setTaskId(task.getTaskId());
            logs.setExecuteTime(new Date(task.getExecuteTime()));
            logs.setVersion(1);
            logs.setStatus(ScheduleConstants.PENDING);
            taskInfoLogsMapper.insert(logs);

            task.setTaskId(logs.getTaskId());

            return true;

        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    private boolean addToCache(Task task){
        try {
            Integer taskType = task.getTaskType();
            Integer priority = task.getPriority();
            String keySuffix = taskType + "_" + priority;
            if (task.getExecuteTime() <= System.currentTimeMillis()){
                String key = ScheduleConstants.TOPIC + keySuffix;
                log.info("addToCache, key:{}", key);
                cacheService.lLeftPush(key, JSON.toJSONString(task));

            } else if (task.getExecuteTime() <= System.currentTimeMillis() + 5 * 60 * 1000){
                String key = ScheduleConstants.FUTURE + keySuffix;
                log.info("addToCache, key:{}", key);
                cacheService.zAdd(key, JSON.toJSONString(task), task.getExecuteTime());
            }

            return true;

        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }


}
