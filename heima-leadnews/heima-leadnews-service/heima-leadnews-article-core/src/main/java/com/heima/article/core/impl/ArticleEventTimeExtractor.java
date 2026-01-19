package com.heima.article.core.impl;


import com.alibaba.fastjson.JSON;
import com.heima.model.articlecore.dto.ArticleBehaviorMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;


@Slf4j
public class ArticleEventTimeExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {

        if (record.value() == null) {
            return partitionTime;
        }

        log.info("extract timestamp={}, value={}", partitionTime, record.value());


        try {
            ArticleBehaviorMsg msg =
                    JSON.parseObject(record.value().toString(), ArticleBehaviorMsg.class);

            if (msg != null && msg.getEventTime() > 0) {
                return msg.getEventTime();
            }

        } catch (Exception e) {
            // ignore
        }

        // fallback：不要返回 0，用 partitionTime
        return partitionTime;
    }


}
