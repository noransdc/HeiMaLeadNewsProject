package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.search.vo.SearchArticleVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;


@Mapper
public interface ApArticleMapper extends BaseMapper<ApArticle> {


    /**
     *
     * @param dto
     * @param type 1加载更多  2加载最新
     * @return
     */
    List<ApArticle> loadArticleList(ArticleHomeDto dto, Short type);

    List<SearchArticleVo> getAllList();

    List<ApArticle> getListByLast5Days(@Param("dayParam") Date dayParam);
}
