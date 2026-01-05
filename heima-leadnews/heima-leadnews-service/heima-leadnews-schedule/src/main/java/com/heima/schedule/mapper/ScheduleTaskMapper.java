package com.heima.schedule.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.schedule.entity.ScheduleTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface ScheduleTaskMapper extends BaseMapper<ScheduleTask> {


    void insertBatch(@Param("taskList") List<ScheduleTask> taskList);

}
