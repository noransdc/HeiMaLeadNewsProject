package com.heima.article.core.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.core.mapper.ArticleContentMapper;
import com.heima.article.core.mapper.ArticleMapper;
import com.heima.article.core.service.ArticleChannelService;
import com.heima.article.core.service.ArticleService;
import com.heima.common.enums.ArticleAuditEnum;
import com.heima.common.enums.ArticleCoverEnum;
import com.heima.common.exception.CustomException;
import com.heima.model.articlecore.dto.ArticleSubmitDto;
import com.heima.model.articlecore.entity.Article;
import com.heima.model.articlecore.entity.ArticleContent;
import com.heima.model.articlecore.entity.ArticleContentItem;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private final String CONTENT_IMG = "image";


    @Autowired
    private ArticleContentMapper articleContentMapper;

    @Autowired
    private ArticleChannelService articleChannelService;


    @Transactional
    @Override
    public void submit(ArticleSubmitDto dto) {
        validateRequestParam(dto);

        ArticleCoverEnum coverEnum = ArticleCoverEnum.codeOf(dto.getCoverType());
        if (coverEnum == null){
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID);
        }

        String coverImgUrlStr = getCoverImgUrl(coverEnum, dto);

        Article article = convertToArticle(dto, coverImgUrlStr);

        save(article);

        ArticleContent articleContent = convertToArticleContent(dto, article);

        articleContentMapper.insert(articleContent);

    }

    private Article convertToArticle(ArticleSubmitDto dto, String coverImgUrlStr){
        Article article = new Article();
        BeanUtils.copyProperties(dto, article);
        article.setCoverImgUrl(coverImgUrlStr);

        if (dto.getIsDraft() == 1){
            article.setAuditStatus(ArticleAuditEnum.DRAFT.getCode());
        } else if (dto.getIsDraft() == 0){
            article.setAuditStatus(ArticleAuditEnum.SUBMITTED.getCode());
        }
        return article;
    }

    private ArticleContent convertToArticleContent(ArticleSubmitDto dto, Article article){
        ArticleContent articleContent = new ArticleContent();
        articleContent.setArticleId(article.getId());
        articleContent.setContent(dto.getContent());
        return articleContent;
    }

    private void validateRequestParam(ArticleSubmitDto dto){
        if (dto == null){
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID);
        }

        if (dto.getAuthorId() == null){
            throw new CustomException(AppHttpCodeEnum.RPC_AUTHOR_ID_NULL);
        }

        Integer isDraft = dto.getIsDraft();

        if (StringUtils.isBlank(dto.getTitle()) || StringUtils.isBlank(dto.getContent()) || dto.getChannelId() == null
                || isDraft == null || dto.getCoverType() == null){
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID);
        }

        if (isDraft != 0 && isDraft != 1){
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID);
        }

        articleChannelService.validateChannel(dto.getChannelId());

    }

    private String getCoverImgUrl(ArticleCoverEnum coverEnum, ArticleSubmitDto dto){
        String urlStr = null;
        List<String> images = dto.getImages();
        switch (coverEnum){
            case AUTO:
                urlStr = getAutoImgUrl(dto);
                break;

            case SINGLE:
                if (images.size() != 1){
                    throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID, "封面图参数错误");
                }
                urlStr = images.get(0);
                break;

            case MULTIPLE:
                if (images.size() > 3){
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

    private String getAutoImgUrl(ArticleSubmitDto dto){
        List<ArticleContentItem> itemList;
        try {
            itemList = JSON.parseArray(dto.getContent(), ArticleContentItem.class);

        } catch (Exception e){
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID, "文章内容解析错误");
        }

        if (CollectionUtils.isEmpty(itemList)){
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID, "文章内容解析错误");
        }

        List<String> urlList = new ArrayList<>();
        for (ArticleContentItem item : itemList) {
            if (CONTENT_IMG.equals(item.getType()) && StringUtils.isNotBlank(item.getValue())){
                urlList.add(item.getValue());
            }
        }

        if (urlList.isEmpty()){
            return null;
        }

        List<String> limitList = urlList.stream().limit(3).collect(Collectors.toList());

        return StringUtils.join(limitList, ",");
    }


}
