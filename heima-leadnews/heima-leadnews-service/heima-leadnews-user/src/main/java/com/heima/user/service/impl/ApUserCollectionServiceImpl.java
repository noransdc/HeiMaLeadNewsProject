package com.heima.user.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.JsonObject;
import com.heima.common.exception.CustomException;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.ApCollectionDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.user.pojos.ApUserCollection;
import com.heima.thread.AppThreadLocalUtil;
import com.heima.user.constant.EventAggregateEnum;
import com.heima.user.constant.EventTypeEnum;
import com.heima.user.mapper.ApUserCollectionMapper;
import com.heima.user.service.ApUserCollectionService;
import com.heima.user.service.EventOutboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ApUserCollectionServiceImpl extends ServiceImpl<ApUserCollectionMapper, ApUserCollection>
        implements ApUserCollectionService {


    @Autowired
    private EventOutboxService eventOutboxService;


    @Override
    @Transactional
    public void collectArticle(ApCollectionDto dto) {
        if (dto.getEntryId() == null){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        if (dto.getOperation() != 0 && dto.getOperation() != 1){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null){
            throw new CustomException(AppHttpCodeEnum.USER_NOT_EXIST);
        }

        if (dto.getOperation() == 1){
            addArticleCollection(dto, user.getId());
        } else {
            cancelArticleCollection(dto, user.getId());
        }


    }

    private void addArticleCollection(ApCollectionDto dto, Long userId){
        ApUserCollection collection = new ApUserCollection();
        collection.setUserId(userId);
        collection.setArticleId(dto.getEntryId());

        try {
            save(collection);

        } catch (DuplicateKeyException e){
            // 已收藏，幂等处理
            return;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("articleId", dto.getEntryId());
        String json = jsonObject.toJSONString();

        eventOutboxService.addEvent(EventTypeEnum.ARTICLE_COLLECTION.name(), EventAggregateEnum.ARTICLE.name(),
                String.valueOf(dto.getEntryId()), json);

    }

    private void cancelArticleCollection(ApCollectionDto dto, Long userId){
        LambdaQueryWrapper<ApUserCollection> query = Wrappers.lambdaQuery();
        query.eq(ApUserCollection::getUserId, userId)
                .eq(ApUserCollection::getArticleId, dto.getEntryId());
        boolean removed = remove(query);

        if (!removed){
            // 本来就没收藏，幂等处理
            return;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("articleId", dto.getEntryId());
        String json = jsonObject.toJSONString();

        eventOutboxService.addEvent(EventTypeEnum.ARTICLE_CANCEL_COLLECTION.name(), EventAggregateEnum.ARTICLE.name(),
                String.valueOf(dto.getEntryId()), json);

    }



}
