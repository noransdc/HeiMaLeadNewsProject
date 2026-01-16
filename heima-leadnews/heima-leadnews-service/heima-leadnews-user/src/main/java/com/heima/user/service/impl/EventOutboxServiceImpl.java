package com.heima.user.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.exception.CustomException;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.ApCollectionDto;
import com.heima.model.user.entity.EventOutbox;
import com.heima.model.user.pojos.ApUser;
import com.heima.thread.AppThreadLocalUtil;
import com.heima.user.mapper.EventOutboxMapper;
import com.heima.user.service.EventOutboxService;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class EventOutboxServiceImpl extends ServiceImpl<EventOutboxMapper, EventOutbox>
        implements EventOutboxService {


    @Override
    public void addEvent(String eventType, String aggregateType, String aggregateId, String payload) {
        EventOutbox eventOutbox = new EventOutbox();
        eventOutbox.setEventId(UUID.randomUUID().toString().replace("-", ""));
        eventOutbox.setEventType(eventType);
        eventOutbox.setAggregateType(aggregateType);
        eventOutbox.setAggregateId(aggregateId);
        eventOutbox.setPayload(payload);

        save(eventOutbox);
    }


}
