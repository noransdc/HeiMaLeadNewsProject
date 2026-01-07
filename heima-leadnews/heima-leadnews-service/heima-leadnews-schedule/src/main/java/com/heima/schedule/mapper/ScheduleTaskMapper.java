package com.heima.schedule.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.schedule.entity.ScheduleTask;
import com.heima.schedule.constant.TaskStatusEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;


@Mapper
public interface ScheduleTaskMapper extends BaseMapper<ScheduleTask> {


    void insertBatch(@Param("taskList") List<ScheduleTask> taskList);

    List<ScheduleTask> pickAuditExecutableTasks(@Param("executeTime") Date executeTime);

    int markStatusBatch(@Param("tasks") List<ScheduleTask> tasks,
                        @Param("runningStatus") int runningStatus,
                        @Param("initStatus") int initStatus,
                        @Param("failStatus") int failStatus
    );

    void markSuccess(@Param("taskId") Long taskId,
                     @Param("successStatus") int successStatus,
                     @Param("runningStatus") int runningStatus);


    void markFailRetryable(@Param("taskId") Long taskId,
                  @Param("failStatus") int failStatus,
                  @Param("runningStatus") int runningStatus,
                  @Param("errorMsg") String errorMsg);

    void markFailUnrecoverable(@Param("taskId") Long taskId,
                  @Param("failStatus") int deadStatus,
                  @Param("runningStatus") int runningStatus,
                  @Param("errorMsg") String errorMsg
                  );

}
