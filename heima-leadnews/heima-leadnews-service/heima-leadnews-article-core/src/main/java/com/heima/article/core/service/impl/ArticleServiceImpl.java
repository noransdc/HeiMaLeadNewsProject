package com.heima.article.core.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.apis.schedule.ScheduleTaskClient;
import com.heima.article.core.constant.ArticleTaskType;
import com.heima.article.core.mapper.ArticleContentMapper;
import com.heima.article.core.mapper.ArticleMapper;
import com.heima.article.core.service.ArticleAuditService;
import com.heima.article.core.service.ArticleChannelService;
import com.heima.article.core.service.ArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.enums.ArticleAuditEnum;
import com.heima.common.enums.ArticleCoverEnum;
import com.heima.common.exception.CustomException;
import com.heima.model.articlecore.dto.ArticleDetailDto;
import com.heima.model.articlecore.dto.ArticlePublishDto;
import com.heima.model.articlecore.dto.ArticleSubmitDto;
import com.heima.model.articlecore.entity.Article;
import com.heima.model.articlecore.entity.ArticleContent;
import com.heima.model.articlecore.entity.ArticleContentItem;
import com.heima.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {


    @Autowired
    private ArticleContentMapper articleContentMapper;

    @Autowired
    private ArticleChannelService articleChannelService;

    @Autowired
    private ArticleAuditService articleAuditService;

    @Autowired
    private ScheduleTaskClient scheduleTaskClient;


    @Transactional
    @Override
    public void submit(ArticleSubmitDto dto) {
        validateRequestParam(dto);

        ArticleCoverEnum coverEnum = ArticleCoverEnum.codeOf(dto.getCoverType());
        if (coverEnum == null) {
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID);
        }

        String coverImgUrlStr = getCoverImgUrl(coverEnum, dto);

        Article article = convertToArticle(dto, coverImgUrlStr);

        save(article);

        ArticleContent articleContent = convertToArticleContent(dto, article);

        articleContentMapper.insert(articleContent);

        articleAuditService.audit(article.getId());
    }

    @Override
    public void updateAuditStatus(Long articleId, ArticleAuditEnum status, String reason) {
        if (articleId == null) {
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID);
        }

        Article article = getValidArticle(articleId);

        ArticleAuditEnum current = ArticleAuditEnum.codeOf(article.getAuditStatus());

        if (current == null) {
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID);
        }

        if (!current.canTransitTo(status)) {
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID,
                    "非法的审核状态迁移：" + current + " -> " + status);
        }

        article.setAuditStatus(status.getCode());
        article.setRejectReason(reason);

        updateById(article);

    }


    @Override
    public ArticleDetailDto getArticleDetail(Long articleId) {
        if (articleId == null) {
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID);
        }
        Article article = getById(articleId);

        ArticleContent articleContent = articleContentMapper.selectById(articleId);

        if (article == null || articleContent == null) {
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID, "文章不存在");
        }

        ArticleDetailDto articleDetailDto = new ArticleDetailDto();
        articleDetailDto.setArticle(article);
        articleDetailDto.setArticleContent(articleContent);

        return articleDetailDto;
    }

    @Override
    public void scanPendingAuditList() {
        List<Article> articleList = lambdaQuery().eq(Article::getAuditStatus, ArticleAuditEnum.PENDING_AUDIT.getCode())
                .eq(Article::getIsEnabled, 1)
                .eq(Article::getIsDelete, 0)
                .orderByDesc(Article::getPublishTime)
                .list();

        addToScheduleTask(articleList);

    }

    private void addToScheduleTask(List<Article> articleList){
//        long current = System.currentTimeMillis();
//        for (Article article : articleList) {
//            long publishTime = article.getPublishTime().getTime();
//            if (publishTime <= current) {
//
//            } else if (publishTime <= current + 5 * 60 * 1000) {
//
//            }
//        }


        List<ArticlePublishDto> dtoList = new ArrayList<>();
        for (Article article : articleList) {
            ArticlePublishDto dto = new ArticlePublishDto();
            dto.setArticleId(article.getId());
            dto.setPublishTime(article.getPublishTime());
            dto.setAction(ArticleTaskType.ARTICLE_AUDIT);
            dtoList.add(dto);
        }

        scheduleTaskClient.addScheduleTasks(dtoList);
    }

    private Article getValidArticle(Long articleId) {
        Article article = lambdaQuery().eq(Article::getId, articleId)
                .eq(Article::getIsEnabled, 1)
                .eq(Article::getIsDelete, 0)
                .one();

        if (article == null) {
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID, "该文章无效");
        }

        return article;
    }

    private Article convertToArticle(ArticleSubmitDto dto, String coverImgUrlStr) {
        Article article = new Article();
        BeanUtils.copyProperties(dto, article);
        article.setCoverImgUrl(coverImgUrlStr);

        if (dto.getIsDraft() == 1) {
            article.setAuditStatus(ArticleAuditEnum.DRAFT.getCode());
        } else if (dto.getIsDraft() == 0) {
            article.setAuditStatus(ArticleAuditEnum.PENDING_AUDIT.getCode());
        }
        return article;
    }

    private ArticleContent convertToArticleContent(ArticleSubmitDto dto, Article article) {
        ArticleContent articleContent = new ArticleContent();
        articleContent.setArticleId(article.getId());
        articleContent.setContent(dto.getContent());
        return articleContent;
    }

    private void validateRequestParam(ArticleSubmitDto dto) {
        if (dto == null) {
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID);
        }

        if (dto.getAuthorId() == null) {
            throw new CustomException(AppHttpCodeEnum.RPC_AUTHOR_ID_NULL);
        }

        Integer isDraft = dto.getIsDraft();

        if (StringUtils.isBlank(dto.getTitle()) || StringUtils.isBlank(dto.getContent()) || dto.getChannelId() == null
                || isDraft == null || dto.getCoverType() == null) {
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID);
        }

        if (isDraft != 0 && isDraft != 1) {
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID);
        }

        articleChannelService.validateChannel(dto.getChannelId());

    }

    private String getCoverImgUrl(ArticleCoverEnum coverEnum, ArticleSubmitDto dto) {
        String urlStr = null;
        List<String> images = dto.getImages();
        switch (coverEnum) {
            case AUTO:
                urlStr = getAutoImgUrl(dto);
                break;

            case SINGLE:
                if (images.size() != 1) {
                    throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID, "封面图参数错误");
                }
                urlStr = images.get(0);
                break;

            case MULTIPLE:
                if (images.size() > 3) {
                    throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID, "封面图参数错误");
                }
                urlStr = StringUtils.join(images, ",");
                break;

            case NONE:
            default:
                break;
        }

        return urlStr;
    }

    private String getAutoImgUrl(ArticleSubmitDto dto) {
        List<ArticleContentItem> itemList;
        try {
            itemList = JSON.parseArray(dto.getContent(), ArticleContentItem.class);

        } catch (Exception e) {
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID, "文章内容解析错误");
        }

        if (CollectionUtils.isEmpty(itemList)) {
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID, "文章内容解析错误");
        }

        List<String> urlList = new ArrayList<>();
        for (ArticleContentItem item : itemList) {
            if (ArticleConstants.CONTENT_ITEM_IMG.equals(item.getType()) && StringUtils.isNotBlank(item.getValue())) {
                urlList.add(item.getValue());
            }
        }

        if (urlList.isEmpty()) {
            return null;
        }

        List<String> limitList = urlList.stream().limit(3).collect(Collectors.toList());

        return StringUtils.join(limitList, ",");
    }


}
