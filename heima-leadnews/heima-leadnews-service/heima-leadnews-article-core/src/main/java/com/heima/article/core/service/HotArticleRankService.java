package com.heima.article.core.service;


import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.articlecore.dto.ArticleBehaviorWindowResult;
import com.heima.model.articlecore.vo.AuthorArticleListVo;
import com.heima.model.articlecore.vo.FrontArticleListVo;

import java.util.List;

public interface HotArticleRankService {


    void refreshHotRank(ArticleBehaviorWindowResult windowResult);

    List<FrontArticleListVo> getHotArticlesByChannel(ArticleHomeDto dto);


}
