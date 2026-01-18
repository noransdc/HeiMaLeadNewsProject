package com.heima.article.core.listener;


import com.alibaba.fastjson.JSON;
import com.heima.common.constants.ArticleBehaviorConstant;
import com.heima.common.enums.EventTypeEnum;
import com.heima.model.articlecore.dto.ArticleBehaviorAgg;
import com.heima.model.articlecore.dto.ArticleBehaviorMsg;
import com.heima.model.articlecore.dto.ArticleBehaviorWindowResult;
import com.heima.model.user.entity.EventOutbox;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.time.Duration;


@Slf4j
@Configuration
public class ArticleBehaviorStreamProcessor {


    @Bean
    public KStream<String, String> kafkaStreams(StreamsBuilder streamsBuilder) {
        KStream<String, String> source = streamsBuilder.stream(ArticleBehaviorConstant.ARTICLE_BEHAVIOR_STREAM);

        JsonSerde<ArticleBehaviorAgg> aggSerde =
                new JsonSerde<>(ArticleBehaviorAgg.class);

        source
                // 1. 先打日志，确认真的收到消息
                .peek((key, value)->{
                    log.info("raw consume, key={}, value={}", key, value);
                })
                .selectKey((key, value) -> {
                    if (value == null){
                        return null;
                    }

                    try {
                        ArticleBehaviorMsg msg = JSON.parseObject(value, ArticleBehaviorMsg.class);
                        if (msg == null || msg.getArticleId() == null){
                            return null;
                        }
                        return String.valueOf(msg.getArticleId());

                    } catch (Exception e){
                        log.error("selectKey parse failed, value={}", value, e);
                        return null;
                    }

                })
                .filter((key, value)-> key != null)
                .groupByKey()
                // 真实项目里“合理使用”的时间窗口
                .windowedBy(TimeWindows.of(Duration.ofSeconds(10)))
                .aggregate(
                        new Initializer<ArticleBehaviorAgg>() {
                            @Override
                            public ArticleBehaviorAgg apply() {
                                return new ArticleBehaviorAgg();
                            }
                        },
                        new Aggregator<String, String, ArticleBehaviorAgg>() {
                            @Override
                            public ArticleBehaviorAgg apply(String articleId, String value, ArticleBehaviorAgg agg) {

                                if (value == null){
                                    return agg;
                                }

                                ArticleBehaviorMsg msg;

                                try {
                                    msg = JSON.parseObject(value, ArticleBehaviorMsg.class);

                                } catch (Exception e){
                                    log.error("aggregate parse failed, value={}", value, e);
                                    return agg;
                                }

                                if (msg == null || msg.getArticleId() == null || msg.getEventType() == null){
                                    return agg;
                                }

                                agg.setArticleId(msg.getArticleId());
                                calculate(agg, msg.getEventType());

                                return agg;
                            }
                        },
                        Materialized.with(Serdes.String(), aggSerde))
                .toStream()
                // 5. 再打一层日志
                .peek((windowedKey, agg)->{
                    log.info(
                            "windowEnd={}, articleId={}, agg={}",
                            windowedKey.window().end(),
                            windowedKey.key(),
                            agg
                    );
                })
                .map((windowedKey, agg)->{
                    if (agg == null){
                        return null;
                    }

                    String articleId = windowedKey.key();
                    long windowEnd = windowedKey.window().end();

                    ArticleBehaviorWindowResult result = new ArticleBehaviorWindowResult();
                    result.setArticleId(Long.parseLong(articleId));
                    result.setWindowEnd(windowEnd);
                    result.setAgg(agg);

                    return new KeyValue<>(articleId, JSON.toJSONString(result));
                })
                .filter((key, value)-> key!= null && value != null)
                .to(ArticleBehaviorConstant.ARTICLE_BEHAVIOR_AGG);

        return source;
    }

    private void calculate(ArticleBehaviorAgg agg, String eventType){
        EventTypeEnum eventTypeEnum = EventTypeEnum.valueOf(eventType);
        switch (eventTypeEnum){
            case ARTICLE_VIEW:
                agg.setViewCount(agg.getViewCount() + 1);
                break;

            case ARTICLE_LIKE:
                agg.setLikeCount(agg.getLikeCount() + 1);
                break;

            case ARTICLE_DISLIKE:
                agg.setLikeCount(Math.max(0, agg.getLikeCount() - 1));
                break;

            case ARTICLE_COMMENT:
                agg.setCommentCount(agg.getCommentCount() + 1);
                break;

            case ARTICLE_COLLECTION:
                agg.setCollectCount(agg.getCollectCount() + 1);
                break;

            case ARTICLE_CANCEL_COLLECTION:
                agg.setCollectCount(Math.max(0, agg.getCollectCount() - 1));
                break;

            default:
                break;
        }
    }


}
