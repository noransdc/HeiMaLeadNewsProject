package com.heima.article.core.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.articlecore.entity.ArticleInteraction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface ArticleInteractionMapper extends BaseMapper<ArticleInteraction> {


    int increaseView(@Param("articleId") Long articleId);

    int increaseLike(@Param("articleId") Long articleId);

    int decreaseLike(@Param("articleId") Long articleId);

    int increaseComment(@Param("articleId") Long articleId);

    int increaseCollection(@Param("articleId") Long articleId);

    int decreaseCollection(@Param("articleId") Long articleId);


}
