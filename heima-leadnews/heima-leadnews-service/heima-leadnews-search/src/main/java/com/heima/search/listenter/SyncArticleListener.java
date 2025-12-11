package com.heima.search.listenter;


import com.alibaba.fastjson.JSON;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.constants.ElasticSearchConstant;
import com.heima.model.search.vo.SearchArticleVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class SyncArticleListener {

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    @KafkaListener(topics = ArticleConstants.ARTICLE_ES_SYNC_TOPIC)
    public void onMessage(String message) throws Exception{
        if (StringUtils.isBlank(message)){
            return;
        }
        SearchArticleVo searchArticleVo = JSON.parseObject(message, SearchArticleVo.class);
        IndexRequest indexRequest = new IndexRequest(ElasticSearchConstant.APP_INFO_ARTICLE);
        indexRequest.id(searchArticleVo.getId().toString())
                .source(message, XContentType.JSON);

        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }

}
