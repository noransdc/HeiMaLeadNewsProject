package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ArticleVisitStreamMsg;
import com.heima.model.article.vo.HotArticleVo;
import com.heima.model.articlecore.vo.AuthorArticleListVo;
import com.heima.model.articlecore.vo.FrontArticleListVo;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ApArticleService extends IService<ApArticle> {


    /**
     *
     * @param dto
     * @param type 1加载更多  2加载最新
     * @return
     */
    ResponseResult load(ArticleHomeDto dto, Short type);

    ResponseResult load2(ArticleHomeDto dto, Short type, Boolean firstPage);

    ResponseResult saveArticle(ArticleDto dto);

    void calculateArticleScore();

    void updateRedisHotArticle(ArticleVisitStreamMsg msg);

    List<FrontArticleListVo> getHotList(ArticleHomeDto dto);

}
