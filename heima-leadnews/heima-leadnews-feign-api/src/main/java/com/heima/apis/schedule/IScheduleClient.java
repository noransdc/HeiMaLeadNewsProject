package com.heima.apis.schedule;


import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.schedule.pojos.Task;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(value = "leadnews-schedule")
public interface IScheduleClient {

    @PostMapping("/api/v1/schedule/add")
    ResponseResult addTask(@RequestBody Task task);

    @GetMapping("/api/vi/schedule/task/{type}/{priority}")
    ResponseResult<Task> pollTask(@PathVariable Integer type, @PathVariable Integer priority);
}
