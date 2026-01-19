package com.heima.user.service.impl;


import com.alibaba.fastjson.JSON;
import com.heima.common.constants.ArticleBehaviorConstant;
import com.heima.model.articlecore.dto.ArticleBehaviorMsg;
import com.heima.model.user.entity.EventOutbox;
import com.heima.user.service.EventOutboxSenderService;
import com.heima.user.service.EventOutboxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.List;


@Slf4j
@Service
public class EventOutboxSenderServiceImpl implements EventOutboxSenderService {

    @Autowired
    private EventOutboxService eventOutboxService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(initialDelay = 10 * 1000, fixedDelay = 10 * 1000)
    public void sendEvent() {
        List<EventOutbox> list = eventOutboxService.listNew();

        for (EventOutbox item : list) {

            String eventId = item.getEventId();
            boolean success = eventOutboxService.markSending(eventId);
            if (!success) {
                continue;
            }

            try {
                ArticleBehaviorMsg msg = new ArticleBehaviorMsg();
                msg.setEventId(eventId);
                msg.setArticleId(Long.parseLong(item.getAggregateId()));
                msg.setEventType(item.getEventType());

                long timestamp = item.getCreateTime()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli();

                msg.setEventTime(timestamp);

                kafkaTemplate.send(ArticleBehaviorConstant.ARTICLE_BEHAVIOR_EVENT, JSON.toJSONString(msg));

                eventOutboxService.markSent(eventId);

                log.info("kafkaTemplate.send:{}", JSON.toJSONString(msg));

            } catch (Exception e) {
                eventOutboxService.markFailed(eventId);
            }

        }


    }


}
