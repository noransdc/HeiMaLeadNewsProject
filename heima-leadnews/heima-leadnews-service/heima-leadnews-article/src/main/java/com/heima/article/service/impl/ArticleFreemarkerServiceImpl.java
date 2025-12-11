package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.common.constants.ArticleConstants;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.search.vo.SearchArticleVo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
@Transactional
public class ArticleFreemarkerServiceImpl implements ArticleFreemarkerService {

    @Autowired
    private Configuration configuration;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ApArticleService apArticleService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    @Async
    public void buildArticleToMinIo(ApArticle apArticle, String content) {
        try {
            if (StringUtils.isBlank(content)){
                return;
            }

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("title", apArticle.getTitle());
            dataMap.put("author", apArticle.getAuthorName());
            dataMap.put("publishTime", apArticle.getPublishTime().toString());
            dataMap.put("createdTime", apArticle.getCreatedTime().toString());
            dataMap.put("content", JSONArray.parseArray(content));

            StringWriter stringWriter = new StringWriter();

            Template template = configuration.getTemplate("articlev2.ftl");
            template.process(dataMap, stringWriter);

            InputStream inputStream = new ByteArrayInputStream(stringWriter.toString().getBytes());
            String url = fileStorageService.uploadHtmlFile("", apArticle.getId() + ".html", inputStream);

            log.info("freemarker url:{}", url);

            apArticleService.update(Wrappers.<ApArticle>lambdaUpdate().eq(ApArticle::getId, apArticle.getId())
                    .set(ApArticle::getStaticUrl, url));

            sendArticleToES(apArticle, content, url);

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void sendArticleToES(ApArticle apArticle, String content, String url){
        SearchArticleVo searchArticleVo = new SearchArticleVo();
        BeanUtils.copyProperties(apArticle, searchArticleVo);
        searchArticleVo.setContent(content);
        searchArticleVo.setStaticUrl(url);
        kafkaTemplate.send(ArticleConstants.ARTICLE_ES_SYNC_TOPIC, JSON.toJSONString(searchArticleVo));
    }

}
