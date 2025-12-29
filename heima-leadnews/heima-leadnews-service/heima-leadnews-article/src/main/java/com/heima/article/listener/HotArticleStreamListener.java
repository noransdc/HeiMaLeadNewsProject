package com.heima.article.listener;


import com.alibaba.fastjson.JSON;
import com.heima.common.constants.HotArticleConstants;
import com.heima.model.article.pojos.ArticleVisitStreamMsg;
import com.heima.model.article.pojos.UpdateArticleMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;


@Configuration
@Slf4j
public class HotArticleStreamListener {


    @Bean
    public KStream<String, String> kStream(StreamsBuilder streamsBuilder) {
        KStream<String, String> kStream = streamsBuilder.stream(HotArticleConstants.HOT_ARTICLE_SCORE_TOPIC);

        kStream.map((key, value) -> {
                    log.debug("map start key={}, value={}", key, value);

                    if (StringUtils.isBlank(value)){
                        log.warn("drop message: blank value");
                        return null;
                    }

                    UpdateArticleMsg msg;

                    try {
                        msg = JSON.parseObject(value, UpdateArticleMsg.class);

                    } catch (Exception e){
                        log.error("drop message: json parse error, value={}", value, e);
                        return null;
                    }

                    if (msg.getArticleId() == null || msg.getType() == null || msg.getAdd() == null){
                        log.warn("drop message: invalid msg={}", msg);
                        return null;
                    }

                    //key="1994038386045882369", value=LIKES:1
                    return new KeyValue<>(msg.getArticleId().toString(), msg.getType().name() + ":" + msg.getAdd());

                })
                .filter((k,v)-> v!=null)
                .groupBy((key, value) -> {

                    log.info("groupBy key:{}", key);
                    log.info("groupBy value:{}", value);

                    return key;
                })
                .windowedBy(TimeWindows.of(Duration.ofSeconds(10)))
                .aggregate(new Initializer<String>() {
                    @Override
                    public String apply() {
                        return "COLLECTION:0,COMMENT:0,LIKES:0,VIEWS:0";
                    }
                }, new Aggregator<String, String, String>() {
                    @Override
                    public String apply(String key, String value, String aggValue) {

                        log.info("Aggregator key:{}", key);
                        log.info("Aggregator value:{}", value);
                        log.info("Aggregator aggValue:{}", aggValue);

                        int likeNum = 0;
                        int viewNum = 0;
                        int commentNum = 0;
                        int collectionNum = 0;

                        String[] aggArr = aggValue.split(",");
                        if (aggArr.length != 4){
                            log.warn("drop message: invalid aggValue={}", aggValue);
                            return aggValue;
                        }

                        for (String arr : aggArr) {
                            String[] split = arr.split(":");
                            if (split.length != 2){
                                continue;
                            }
                            String splitKey = split[0];
                            String splitValue = split[1];
                            UpdateArticleMsg.UpdateArticleType typeEnum = UpdateArticleMsg.UpdateArticleType.valueOf(splitKey);
                            switch (typeEnum) {
                                case COLLECTION:
                                    collectionNum = Integer.parseInt(splitValue);
                                    break;

                                case COMMENT:
                                    commentNum = Integer.parseInt(splitValue);
                                    break;

                                case LIKES:
                                    likeNum = Integer.parseInt(splitValue);
                                    break;

                                case VIEWS:
                                    viewNum = Integer.parseInt(splitValue);
                                    break;

                                default:
                                    break;
                            }
                        }

                        String[] eventArr = value.split(":");
                        if (eventArr.length != 2){
                            return aggValue;
                        }
                        String eventKey = eventArr[0];
                        String eventValue = eventArr[1];
                        UpdateArticleMsg.UpdateArticleType eventTypeEnum = UpdateArticleMsg.UpdateArticleType.valueOf(eventKey);
                        switch (eventTypeEnum) {
                            case COLLECTION:
                                collectionNum += Integer.parseInt(eventValue);
                                break;

                            case COMMENT:
                                commentNum += Integer.parseInt(eventValue);
                                break;

                            case LIKES:
                                likeNum += Integer.parseInt(eventValue);
                                break;

                            case VIEWS:
                                viewNum += Integer.parseInt(eventValue);
                                break;

                            default:
                                break;
                        }

                        String formatStr = String.format("COLLECTION:%d,COMMENT:%d,LIKES:%d,VIEWS:%d",
                                collectionNum, commentNum, likeNum, viewNum);

                        log.info("formatStr:{}", formatStr);

                        return formatStr;
                    }
                }, Materialized.as("hot-atricle-stream-count-001"))
                .toStream()
                .map((windowedKey, value) -> {

                    log.info("map end value:{}", value);

                    String articleId = windowedKey.key();
                    log.info("map end articleId:{}", articleId);

                    //kafka stream 时间窗口开始时间
                    long windowStart = windowedKey.window().start();
                    log.info("map end windowStart:{}", windowStart);

                    //kafka stream 时间窗口结束时间
                    long windowEnd = windowedKey.window().end();
                    log.info("map end windowEnd:{}", windowEnd);

                    String json = JSON.toJSONString(packObject(value, windowEnd));
                    return new KeyValue<>(articleId, json);
                })
                .to(HotArticleConstants.HOT_ARTICLE_INCR_HANDLE_TOPIC);


        return kStream;
    }

    private ArticleVisitStreamMsg packObject(String value, long windowEnd) {
        ArticleVisitStreamMsg msg = new ArticleVisitStreamMsg();
        msg.setWindowEnd(windowEnd);

        String[] initArr = value.split(",");
        for (String arr : initArr) {
            String[] split = arr.split(":");
            String splitKey = split[0];
            String splitValue = split[1];
            UpdateArticleMsg.UpdateArticleType typeEnum = UpdateArticleMsg.UpdateArticleType.valueOf(splitKey);
            switch (typeEnum) {
                case COLLECTION:
                    msg.setCollect(Integer.parseInt(splitValue));
                    break;

                case COMMENT:
                    msg.setComment(Integer.parseInt(splitValue));
                    break;

                case LIKES:
                    msg.setLike(Integer.parseInt(splitValue));
                    break;

                case VIEWS:
                    msg.setView(Integer.parseInt(splitValue));
                    break;

                default:
                    break;
            }
        }

        return msg;
    }


}
