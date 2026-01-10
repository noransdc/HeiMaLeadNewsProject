package com.heima.article.core.service.impl;

import com.heima.article.core.mapper.ArticleMapper;
import com.heima.article.core.service.ArticleConstraintQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ArticleConstraintQueryImpl implements ArticleConstraintQuery {


    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public boolean existArticleUnderChannel(Long channelId) {
        Integer isExist = articleMapper.existArticleUnderChannel(channelId);
        return isExist != null ;
    }


}
