package com.heima.article.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.apis.wemedia.IWeMediaClient;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.article.vo.HotArticleVo;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    private final static Integer MAX_PAGE_SIZE = 50;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    @Autowired
    private ArticleFreemarkerService articleFreemarkerService;

    @Autowired
    private IWeMediaClient weMediaClient;

    @Autowired
    private CacheService cacheService;


    @Override
    public ResponseResult load(ArticleHomeDto dto, Short type) {

        Integer size = dto.getSize();
        if (size == null || size == 0){
            size = 10;
        }

        size = Math.min(size, MAX_PAGE_SIZE);
        dto.setSize(size);

        if (!type.equals(ArticleConstants.LOADTYPE_LOAD_MORE) && !type.equals(ArticleConstants.LOADTYPE_LOAD_NEW)){
            type = ArticleConstants.LOADTYPE_LOAD_MORE;
        }

        if (StringUtils.isEmpty(dto.getTag())){
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }

        if (dto.getMaxBehotTime() == null){
            dto.setMaxBehotTime(new Date());
        }

        if (dto.getMinBehotTime() == null){
            dto.setMinBehotTime(new Date());
        }

        List<ApArticle> apArticles = apArticleMapper.loadArticleList(dto, type);

        return ResponseResult.okResult(apArticles);
    }

    @Override
    public ResponseResult load2(ArticleHomeDto dto, Short type, Boolean firstPage) {
        if (!firstPage){
            return load(dto, type);
        }

        String json = cacheService.get(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + dto.getTag());
        if (StringUtils.isNotBlank(json)){
            List<HotArticleVo> articleList = JSON.parseArray(json, HotArticleVo.class);
            if (!CollectionUtils.isEmpty(articleList)){
                return ResponseResult.okResult(articleList);
            }
        }

        return load(dto, type);
    }

    @Override
    public ResponseResult saveArticle(ArticleDto dto) {
        if (dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(dto, apArticle);

        if (apArticle.getId() == null){
            //save
            save(apArticle);
            Long articleId = apArticle.getId();

            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setArticleId(articleId);
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.insert(apArticleContent);

            ApArticleConfig apArticleConfig = new ApArticleConfig(articleId);
            apArticleConfigMapper.insert(apArticleConfig);

        } else {
            //update
            updateById(apArticle);

            LambdaQueryWrapper<ApArticleContent> wrapper = new LambdaQueryWrapper<>();
            ApArticleContent apArticleContent = wrapper.eq(ApArticleContent::getArticleId, apArticle.getId()).getEntity();
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.updateById(apArticleContent);

        }

        articleFreemarkerService.buildArticleToMinIo(apArticle, dto.getContent());

        return ResponseResult.okResult(apArticle.getId());
    }

    @Override
    public void calculateArticleScore() {

        Date dateParam = DateTime.now().minusDays(5).toDate();
        List<ApArticle> lastList = apArticleMapper.getListByLast5Days(dateParam);

        List<HotArticleVo> hotList = new ArrayList<>();
        for (ApArticle apArticle : lastList) {
            int score = calculateSingleScore(apArticle);
            HotArticleVo hotItem = new HotArticleVo();
            BeanUtils.copyProperties(apArticle, hotItem);
            hotItem.setScore(score);
            hotList.add(hotItem);
        }

        String allKey = ArticleConstants.HOT_ARTICLE_FIRST_PAGE + ArticleConstants.DEFAULT_TAG;
        cacheToRedisByChannel(allKey, hotList);

        processHotListByChannel(hotList);

    }

    private void cacheToRedisByChannel(String key, List<HotArticleVo> hotList){
        List<HotArticleVo> allLimitList = hotList.stream()
                .sorted(Comparator.comparing(HotArticleVo::getScore).reversed())
                .limit(30)
                .collect(Collectors.toList());

        cacheService.set(key, JSON.toJSONString(allLimitList));
    }

    private void processHotListByChannel(List<HotArticleVo> hotList){
        List<WmChannel> channelList = weMediaClient.getChannelList();
        if (CollectionUtils.isEmpty(channelList)){
            return;
        }
        List<WmChannel> safeChannelList = channelList.stream()
                .filter(d -> d.getId() != null)
                .collect(Collectors.toList());

        Map<Integer, List<HotArticleVo>> channelArticleMap = hotList.stream()
                .filter(d -> d.getChannelId() != null)
                .collect(Collectors.groupingBy(HotArticleVo::getChannelId));

        for (WmChannel channel : safeChannelList) {
            List<HotArticleVo> singleList = channelArticleMap.get(channel.getId());
            if (!CollectionUtils.isEmpty(singleList)){
                String channelKey = ArticleConstants.HOT_ARTICLE_FIRST_PAGE + channel.getId();
                cacheToRedisByChannel(channelKey, singleList);
            }
        }

    }

    private int calculateSingleScore(ApArticle apArticle){
        Integer view = apArticle.getViews();
        Integer like = apArticle.getLikes();
        Integer comment = apArticle.getComment();
        Integer collection = apArticle.getCollection();

        int sum = 0;
        if (view != null){
            sum += view;
        }

        if (like != null){
            sum += like * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT;
        }

        if (comment != null){
            sum += comment * ArticleConstants.HOT_ARTICLE_COMMENT_WEIGHT;
        }

        if (collection != null){
            sum += collection * ArticleConstants.HOT_ARTICLE_COLLECTION_WEIGHT;
        }

        return sum;
    }


}
