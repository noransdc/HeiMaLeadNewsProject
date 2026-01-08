//package com.heima.wemedia.service.impl;
//
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.heima.apis.schedule.ScheduleTaskClient;
//import com.heima.common.enums.TaskTypeEnum;
//import com.heima.model.common.dtos.ResponseResult;
//import com.heima.model.common.enums.AppHttpCodeEnum;
//import com.heima.model.schedule.pojos.Task;
//import com.heima.model.wemedia.pojos.WmNews;
//import com.heima.utils.common.ProtostuffUtil;
//import com.heima.wemedia.mapper.WmNewsMapper;
//import com.heima.wemedia.service.WmAutoScanService;
//import com.heima.wemedia.service.WmScheduleService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.Date;
//
//
//@Service
//@Slf4j
//public class WmScheduleServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmScheduleService {
//
//
//    @Autowired
//    private ScheduleTaskClient scheduleClient;
//
//
//    @Override
//    @Async
//    public void addNewsToTask(Integer id, LocalDateTime publishTime) {
//        Task task = new Task();
//        task.setTaskType(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType());
//        task.setPriority(TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
////        task.setExecuteTime(publishTime.getTime());
//        WmNews wmNews = new WmNews();
//        wmNews.setId(id);
//        byte[] bytes = ProtostuffUtil.serialize(wmNews);
//        task.setParameters(bytes);
//
//        scheduleClient.addTask(task);
//    }
//
//
////    @Scheduled(cron = "0 0/3 * * * ?")
////    public void pollTask(){
////        Integer type = TaskTypeEnum.NEWS_SCAN_TIME.getTaskType();
////        Integer priority = TaskTypeEnum.NEWS_SCAN_TIME.getPriority();
////        ResponseResult result = scheduleClient.pollTask(type, priority);
////
////        if (result == null){
////            return;
////        }
////        log.info("pollTask, result:{}", result.getData());
////
////        Integer code = result.getCode();
////        if (code == null || !code.equals(AppHttpCodeEnum.SUCCESS.getCode())){
////            return;
////        }
////        Object data = result.getData();
////        boolean checkCls= data instanceof Task;
////        if (!checkCls){
////            return;
////        }
////        Task task  = (Task) data;
////        byte[] parameters = task.getParameters();
////        if (parameters == null){
////            return;
////        }
////        WmNews wmNews = ProtostuffUtil.deserialize(parameters, WmNews.class);
////        log.info("pollTask, wmNews.getId:{}", wmNews.getId());
////        wmAutoScanService.autoScanWmNews(wmNews.getId());
////    }
//
//}
