package com.heima.article.core.schedule;


import com.heima.article.core.service.ArticleService;
import com.heima.article.core.service.HotArticleRankService;
import com.heima.model.articlecore.entity.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
public class ArticleDisableCacheCleanupJob {


    @Autowired
    private ArticleService articleService;

    @Autowired
    private HotArticleRankService hotArticleRankService;

    @Scheduled(initialDelay = 10 * 1000L, fixedDelay = 5 * 60 * 1000L)
    public void cleanDownArticleCache(){
        List<Article> list = articleService.listForDisable();
        for (Article article : list) {
            try {
                hotArticleRankService.handleArticleDisabled(article.getId(), article.getChannelId());

            } catch (Exception e) {
                log.error("clean down article cache failed, articleId={}", article.getId(), e);
            }
        }

    }


}
