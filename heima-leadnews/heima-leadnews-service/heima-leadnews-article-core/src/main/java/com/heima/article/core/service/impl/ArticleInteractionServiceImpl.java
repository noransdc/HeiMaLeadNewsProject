package com.heima.article.core.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.core.mapper.ArticleInteractionMapper;
import com.heima.article.core.service.ArticleInteractionService;
import com.heima.common.enums.EventTypeEnum;
import com.heima.common.exception.CustomException;
import com.heima.model.articlecore.entity.ArticleInteraction;
import com.heima.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.function.IntSupplier;


@Service
@Slf4j
public class ArticleInteractionServiceImpl extends ServiceImpl<ArticleInteractionMapper, ArticleInteraction>
        implements ArticleInteractionService {


    @Override
    public void add(Long articleId, String eventType) {
        EventTypeEnum typeEnum;

        try {
            typeEnum = EventTypeEnum.valueOf(eventType);

        } catch (IllegalArgumentException e) {
            log.error("invalid eventType, articleId={}, eventType={}",
                    articleId, eventType);
            return;
        }

        switch (typeEnum) {
            case ARTICLE_VIEW:
                updateWithInt(
                        () -> baseMapper.increaseView(articleId),
                        () -> insertNew(articleId));
                break;

            case ARTICLE_LIKE:
                updateWithInt(
                        () -> baseMapper.increaseLike(articleId),
                        () -> insertNew(articleId));
                break;

            case ARTICLE_DISLIKE:
                updateWithInt(
                        () -> baseMapper.decreaseLike(articleId),
                        () -> insertNew(articleId));
                break;

            case ARTICLE_COMMENT:
                updateWithInt(
                        () -> baseMapper.increaseComment(articleId),
                        () -> insertNew(articleId));
                break;

            case ARTICLE_COLLECTION:
                updateWithInt(
                        () -> baseMapper.increaseCollection(articleId),
                        () -> insertNew(articleId));
                break;

            case ARTICLE_CANCEL_COLLECTION:
                updateWithInt(
                        () -> baseMapper.decreaseCollection(articleId),
                        () -> insertNew(articleId));
                break;

            default:
                break;
        }

    }


    @Override
    public ArticleInteraction getByArticleId(Long articleId) {
        ArticleInteraction interaction = getById(articleId);
        if (interaction == null){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        return interaction;
    }

    private void updateWithInt(IntSupplier updateFunc, Runnable insertFunc){
        int rows = updateFunc.getAsInt();
        if (rows == 0){
            try {
                insertFunc.run();

            } catch (DuplicateKeyException e){
                //ignore
            }

            updateFunc.getAsInt();
        }
    }

    private void insertNew(Long articleId){
        ArticleInteraction interaction = new ArticleInteraction();
        interaction.setArticleId(articleId);
        interaction.setViewCount(0);
        interaction.setLikeCount(0);
        interaction.setCommentCount(0);
        interaction.setCollectCount(0);
        save(interaction);
    }

    private void printLog(int rows, Long articleId){
        if (rows == 0){
            log.warn("article not found, articleId={}", articleId);
        }
    }


}
