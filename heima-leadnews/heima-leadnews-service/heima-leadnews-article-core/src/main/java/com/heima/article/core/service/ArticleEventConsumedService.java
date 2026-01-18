package com.heima.article.core.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.articlecore.entity.ArticleEventConsumed;
import org.apache.ibatis.annotations.Mapper;


public interface ArticleEventConsumedService extends IService<ArticleEventConsumed> {


    boolean addEvent(String eventId, String eventType, Long articleId);

}
