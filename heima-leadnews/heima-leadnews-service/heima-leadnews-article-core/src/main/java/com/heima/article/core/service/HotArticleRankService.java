package com.heima.article.core.service;


import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.articlecore.dto.ArticleBehaviorWindowResult;
import com.heima.model.articlecore.vo.AuthorArticleListVo;
import com.heima.model.articlecore.vo.FrontArticleListVo;

import java.util.List;

public interface HotArticleRankService {


    void refreshHotRank(ArticleBehaviorWindowResult windowResult);

    List<FrontArticleListVo> getHotArticlesByChannel(ArticleHomeDto dto);

    /**
     * 文章内容修改后的缓存处理
     *
     * 规则：
     * 1. 失效 article:info 缓存
     * 2. 不直接更新缓存内容
     * 3. 不影响热榜结构
     * 4. 允许短暂不一致
     */
    void handleArticleUpdated(Long articleId);


    /**
     * 文章下架后的缓存处理
     *
     * 规则：
     * 1. 删除 article:info 缓存
     * 2. 从热榜 ZSET 中移除
     * 3. 写入空值标记，防止缓存穿透
     */
    void handleArticleDisabled(Long articleId, Long channelId);



}
