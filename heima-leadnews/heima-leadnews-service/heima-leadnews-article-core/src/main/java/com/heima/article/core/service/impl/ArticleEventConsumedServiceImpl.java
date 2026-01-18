package com.heima.article.core.service.impl;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.core.mapper.ArticleEventConsumedMapper;
import com.heima.article.core.service.ArticleEventConsumedService;
import com.heima.article.core.service.ArticleInteractionService;
import com.heima.common.exception.CustomException;
import com.heima.model.articlecore.entity.ArticleEventConsumed;
import com.heima.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
public class ArticleEventConsumedServiceImpl extends ServiceImpl<ArticleEventConsumedMapper, ArticleEventConsumed>
        implements ArticleEventConsumedService {

    @Autowired
    private ArticleInteractionService articleInteractionService;

    @Override
    @Transactional
    public boolean addEvent(String eventId, String eventType, Long articleId) {
        if (StringUtils.isBlank(eventId) || StringUtils.isBlank(eventType) || articleId == null){
            log.error("invalid event, eventId={}, articleId={}", eventId, articleId);
            return false;
        }

        ArticleEventConsumed articleEventConsumed = new ArticleEventConsumed();
        articleEventConsumed.setEventId(eventId);
        articleEventConsumed.setEventType(eventType);
        articleEventConsumed.setArticleId(articleId);

        try {
            save(articleEventConsumed);
        } catch (DuplicateKeyException e) {
            // 核心：幂等命中
            log.info("duplicate event, ignore. eventId={}", eventId);
            return false;
        }

        articleInteractionService.add(articleId, eventType);

        return true;
    }


}
