package com.heima.user.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.user.entity.EventOutbox;
import com.heima.user.constant.EventOutboxStatusEnum;
import com.heima.user.mapper.EventOutboxMapper;
import com.heima.user.service.EventOutboxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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


    @Override
    @Transactional
    public List<EventOutbox> listNew() {
        return baseMapper.listByStatus(EventOutboxStatusEnum.NEW.getCode(), 10);
    }

    @Override
    public boolean markSending(String eventId) {
        int count = baseMapper.markSending(eventId,
                EventOutboxStatusEnum.SENDING.getCode(),
                EventOutboxStatusEnum.NEW.getCode());
        return count == 1;
    }

    @Override
    public void markSent(String eventId) {
        baseMapper.markSent(eventId,
                EventOutboxStatusEnum.SENT.getCode(),
                EventOutboxStatusEnum.SENDING.getCode());
    }

    @Override
    public void markFailed(String eventId) {
        baseMapper.markFailed(eventId,
                EventOutboxStatusEnum.FAILED.getCode(),
                EventOutboxStatusEnum.SENDING.getCode());
    }


}
