package com.heima.article.core.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.core.mapper.ArticleChannelMapper;
import com.heima.article.core.mapper.ArticleContentMapper;
import com.heima.article.core.mapper.ArticleMapper;
import com.heima.article.core.service.ArticleChannelService;
import com.heima.article.core.service.ArticleService;
import com.heima.common.enums.ArticleAuditEnum;
import com.heima.common.enums.ArticleCoverEnum;
import com.heima.common.exception.CustomException;
import com.heima.model.articlecore.dto.ArticleSubmitDto;
import com.heima.model.articlecore.entity.Article;
import com.heima.model.articlecore.entity.ArticleChannel;
import com.heima.model.articlecore.entity.ArticleContent;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {


    @Autowired
    private ArticleContentMapper articleContentMapper;

    @Autowired
    private ArticleChannelService articleChannelService;


    @Transactional
    @Override
    public void submit(ArticleSubmitDto dto) {
        if (dto == null){
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID);
        }

        Long authorId = dto.getAuthorId();
        if (authorId == null){
            throw new CustomException(AppHttpCodeEnum.RPC_AUTHOR_ID_NULL);
        }

        String title = dto.getTitle();
        String content = dto.getContent();
        Long channelId = dto.getChannelId();
        Integer isDraft = dto.getIsDraft();
        Integer coverType = dto.getCoverType();

        if (StringUtils.isBlank(title) || StringUtils.isBlank(content) || channelId == null || isDraft == null
                || coverType == null){
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID);
        }

        if (isDraft != 0 && isDraft != 1){
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID);
        }

        ArticleCoverEnum coverEnum = ArticleCoverEnum.codeOf(coverType);
        if (coverEnum == null){
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID);
        }

        articleChannelService.validateChannel(channelId);

        Article article = new Article();
        BeanUtils.copyProperties(dto, article);

        if (dto.getIsDraft() == 1){
            article.setAuditStatus(ArticleAuditEnum.DRAFT.getCode());
        } else if (dto.getIsDraft() == 0){
            article.setAuditStatus(ArticleAuditEnum.SUBMITTED.getCode());
        }

        save(article);

        ArticleContent articleContent = new ArticleContent();
        articleContent.setArticleId(article.getId());
        articleContent.setContent(dto.getContent());

        articleContentMapper.insert(articleContent);



    }


}
