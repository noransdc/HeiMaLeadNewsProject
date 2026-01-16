package com.heima.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.user.dtos.ApCollectionDto;
import com.heima.model.user.entity.EventOutbox;



public interface EventOutboxService extends IService<EventOutbox> {


    void addEvent(String eventType, String aggregateType, String aggregateId, String payload);

}
