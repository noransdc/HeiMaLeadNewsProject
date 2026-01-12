package com.heima.article.core.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.apis.schedule.ScheduleTaskClient;
import com.heima.article.core.convert.ArticleConvert;
import com.heima.common.constants.ArticleTaskType;
import com.heima.article.core.mapper.ArticleContentMapper;
import com.heima.article.core.mapper.ArticleMapper;
import com.heima.article.core.service.ArticleAuditService;
import com.heima.article.core.service.ArticleChannelService;
import com.heima.article.core.service.ArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.enums.ArticleAuditEnum;
import com.heima.common.enums.ArticleCoverEnum;
import com.heima.common.exception.CustomException;
import com.heima.model.articlecore.dto.*;
import com.heima.model.articlecore.entity.Article;
import com.heima.model.articlecore.entity.ArticleContent;
import com.heima.model.articlecore.entity.ArticleContentItem;
import com.heima.model.articlecore.event.ArticleTaskCreatedEvent;
import com.heima.model.articlecore.vo.AdminArticleVo;
import com.heima.model.articlecore.vo.AuthorArticleVo;
import com.heima.model.common.dtos.PageRequestDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.schedule.dto.ArticleAuditCompensateDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private ScheduleTaskClient scheduleTaskClient;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ArticleAuditService articleAuditService;

    @Autowired
    private ArticleMapper articleMapper;


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

//        articleAuditService.audit(article.getId());

        applicationEventPublisher.publishEvent(new ArticleTaskCreatedEvent(article));

    }

    @Override
    public PageResponseResult<List<AuthorArticleVo>> pageOwnArticles(AuthorArticlePageDto dto) {
        if (dto.getAuthorId() == null){
            throw new CustomException(AppHttpCodeEnum.RPC_AUTHOR_ID_NULL);
        }
        ArticlePageQuery pageQuery = new ArticlePageQuery();
        pageQuery.setAuthorId(dto.getAuthorId());
        pageQuery.setKeyword(dto.getKeyword());
        pageQuery.setChannelId(dto.getChannelId());
        pageQuery.setBeginPubDate(dto.getBeginPubDate());
        pageQuery.setEndPubDate(dto.getEndPubDate());
        pageQuery.setAuditStatus(dto.getStatus());
        pageQuery.setPage(dto.getPage());
        pageQuery.setSize(dto.getSize());

        IPage<Article> pageRsp = listPageGeneric(pageQuery);

        PageResponseResult<List<AuthorArticleVo>> result = new PageResponseResult<>(dto.getPage(), dto.getSize(),
                (int)pageRsp.getTotal());
        result.setData(ArticleConvert.toAuthorVoList(pageRsp.getRecords()));

        return result;
    }

    @Override
    public PageResponseResult<List<AdminArticleVo>> pageAllArticles(AdminArticlePageDto dto) {
        ArticlePageQuery pageQuery = new ArticlePageQuery();
        pageQuery.setKeyword(dto.getKeyword());
        pageQuery.setAuditStatus(dto.getStatus());
        pageQuery.setPage(dto.getPage());
        pageQuery.setSize(dto.getSize());

        IPage<Article> pageRsp = listPageGeneric(pageQuery);

        PageResponseResult<List<AdminArticleVo>> result = new PageResponseResult<>(dto.getPage(), dto.getSize(),
                (int)pageRsp.getTotal());
        result.setData(ArticleConvert.toAdminVoList(pageRsp.getRecords()));

        return result;
    }

    private IPage<Article> listPageGeneric(ArticlePageQuery dto) {
        dto.checkParam();

        LambdaQueryWrapper<Article> query = new LambdaQueryWrapper<>();

        query.eq(dto.getAuthorId() != null, Article::getAuthorId, dto.getAuthorId());
        query.eq(dto.getChannelId() != null, Article::getChannelId, dto.getChannelId());
        query.eq(dto.getAuditStatus() != null, Article::getAuditStatus, dto.getAuditStatus());

        if (StringUtils.isNotBlank(dto.getKeyword())){
            query.like(Article::getTitle, dto.getKeyword());
        }

        LocalDate startDay = dto.getBeginPubDate();
        LocalDate endDay = dto.getEndPubDate();
        if (startDay != null && endDay != null){
            LocalDateTime startTime = startDay.atStartOfDay();
            LocalDateTime endTime = endDay.plusDays(1).atStartOfDay();
            query.ge(Article::getPublishTime, startTime)
                    .lt(Article::getPublishTime, endTime);
        }

        query.eq(Article::getIsDelete, 0)
                .eq(Article::getIsEnabled, 1)
                .orderByDesc(Article::getPublishTime);

        IPage<Article> pageRsp = page(new Page<>(dto.getPage(), dto.getSize()), query);

        return pageRsp;
    }

    @Override
    public Article getArticle(Long id) {
        if (id == null){
            throw new CustomException(AppHttpCodeEnum.RPC_AUTHOR_ID_NULL);
        }

        Article article = lambdaQuery().eq(Article::getId, id)
                .eq(Article::getIsDelete, 0)
                .eq(Article::getIsEnabled, 1)
                .one();

        return article;
    }


    @Override
    public void audit(Long articleId) {
        try {
            ArticleDetailDto articleDetail = getArticleDetail(articleId);
            if (articleDetail.getArticle().getAuditStatus() != ArticleAuditEnum.PENDING_AUDIT.getCode()){
                log.info("article {} already audited, skip", articleId);
                return;
            }
            ArticleAuditRsp auditRsp = articleAuditService.audit(articleDetail);
            updateAuditStatus(articleId, auditRsp.getAuditStatus(), auditRsp.getErrorMsg());

        } catch (CustomException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }


    }

    @Override
    public void publish(Long articleId) {
        Article article = getById(articleId);
        if (article.getAuditStatus() != ArticleAuditEnum.AUDIT_SUCCESS.getCode()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "not ready");
        }

        try {
            updateAuditStatus(articleId, ArticleAuditEnum.PUBLISHED.getCode(), null);

        } catch (CustomException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

    }

    @Override
    public void updateAuditStatus(Long articleId, Integer targetStatus, String reason) {
        if (articleId == null) {
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID);
        }

        Article article = getValidArticle(articleId);

        ArticleAuditEnum currentEnum = ArticleAuditEnum.codeOf(article.getAuditStatus());
        ArticleAuditEnum targetEnum = ArticleAuditEnum.codeOf(targetStatus);

        if (currentEnum == null || targetEnum == null) {
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID);
        }

        if (!currentEnum.canTransitTo(targetEnum)) {
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID,
                    "非法的审核状态迁移：" + currentEnum + " -> " + targetStatus);
        }

        article.setAuditStatus(targetStatus);
        article.setRejectReason(reason);

        updateById(article);

        log.info("updateAuditStatus:{}", article);

    }


    @Override
    public ArticleDetailDto getArticleDetail(Long articleId) {
        if (articleId == null) {
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID);
        }
        Article article = lambdaQuery().eq(Article::getId, articleId)
                .eq(Article::getIsDelete, 0)
                .eq(Article::getIsEnabled, 1)
                .one();

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
    public List<ArticleAuditCompensateDto> getArticleAuditCompensateList(PageRequestDto dto) {
        List<Article> articleList = lambdaQuery()
                .select(Article::getId, Article::getPublishTime)
                .eq(Article::getAuditStatus, ArticleAuditEnum.PENDING_AUDIT.getCode())
                .eq(Article::getIsEnabled, 1)
                .eq(Article::getIsDelete, 0)
                .orderByDesc(Article::getPublishTime)
                .list();

        List<ArticleAuditCompensateDto> taskDtoList = new ArrayList<>();
        for (Article article : articleList) {
            taskDtoList.add(new ArticleAuditCompensateDto(article.getId(), article.getPublishTime()));
        }

        return taskDtoList;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlerArticleTaskCreated(ArticleTaskCreatedEvent event){
        log.info("handlerArticleTaskCreated, event:{}", event);
        ArticleTaskDto dto = convertToArticleTaskDto(event.getArticle());
        try {
            scheduleTaskClient.addScheduleTask(dto);
            log.info("scheduleTaskClient.addScheduleTask dto:{}", dto);

        } catch (Exception e){
            log.info("addToScheduleTask:{}", e.getMessage());
        }

    }

    private ArticleTaskDto convertToArticleTaskDto(Article article){
        ArticleTaskDto dto = new ArticleTaskDto();
        dto.setArticleId(article.getId());
        dto.setPublishTime(article.getPublishTime());
        dto.setAction(ArticleTaskType.ARTICLE_AUDIT);
        return dto;
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
