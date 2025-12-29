package com.heima.article.test;


import com.alibaba.fastjson.JSON;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ElasticSearchConstant;
import com.heima.common.constants.HotArticleConstants;
import com.heima.model.article.pojos.ArticleVisitStreamMsg;
import com.heima.model.article.pojos.UpdateArticleMsg;
import com.heima.model.search.vo.SearchArticleVo;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ApArticleTest {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ApArticleService apArticleService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    public void init() throws Exception{
        BulkRequest bulkRequest = new BulkRequest(ElasticSearchConstant.APP_INFO_ARTICLE);

        List<SearchArticleVo> list = apArticleMapper.getAllList();

        for (SearchArticleVo article : list) {
            IndexRequest indexRequest = new IndexRequest();
            String json = JSON.toJSONString(article);
            indexRequest.id(article.getId().toString())
                    .source(json, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    @Test
    public void testCalculateScore(){
        apArticleService.calculateArticleScore();
    }

    @Test
    public void testKafkaStream(){
        UpdateArticleMsg msg = new UpdateArticleMsg();
        msg.setArticleId(1994038386045882369L);
        msg.setType(UpdateArticleMsg.UpdateArticleType.LIKES);
        msg.setAdd(1);
        kafkaTemplate.send(HotArticleConstants.HOT_ARTICLE_SCORE_TOPIC, JSON.toJSONString(msg));
    }

}
