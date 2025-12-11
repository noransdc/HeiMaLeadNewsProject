package com.heima.article.test;


import com.alibaba.fastjson.JSON;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.vo.EsArticleVo;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ApArticleTest {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void init() throws Exception{
        BulkRequest bulkRequest = new BulkRequest("app_info_article");

        List<EsArticleVo> list = apArticleMapper.getAllList();

        for (EsArticleVo article : list) {
            IndexRequest indexRequest = new IndexRequest();
            String json = JSON.toJSONString(article);
            indexRequest.id(article.getId().toString())
                    .source(json, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }


}
