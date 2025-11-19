package com.heima.article.test;


import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ArticleFreemarkerV1Test {

    @Autowired
    private Configuration configuration;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ApArticleService apArticleService;

    @Test
    public void createStaticUrlTest() {
        try {

            ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery()
                    .eq(ApArticleContent::getArticleId, "1383827787629252610L"));
            if (apArticleContent == null || StringUtils.isBlank(apArticleContent.getContent())){
                return;
            }

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("content", JSONArray.parseArray(apArticleContent.getContent()));

            StringWriter stringWriter = new StringWriter();

            Template template = configuration.getTemplate("article.ftl");
            template.process(dataMap, stringWriter);

            InputStream inputStream = new ByteArrayInputStream(stringWriter.toString().getBytes());
            String url = fileStorageService.uploadHtmlFile("", apArticleContent.getArticleId() + ".html", inputStream);

            apArticleService.update(Wrappers.<ApArticle>lambdaUpdate().eq(ApArticle::getId, apArticleContent.getArticleId())
                    .set(ApArticle::getStaticUrl, url));


        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
