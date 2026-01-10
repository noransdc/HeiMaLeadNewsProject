package com.heima.article.core.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.articlecore.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface ArticleMapper extends BaseMapper<Article> {


    Integer existArticleUnderChannel(@Param("channelId") Long channelId);


}
