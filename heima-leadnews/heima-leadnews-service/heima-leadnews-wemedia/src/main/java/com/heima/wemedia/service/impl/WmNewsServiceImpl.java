package com.heima.wemedia.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.WeMediaConstants;
import com.heima.common.constants.WmNewsMessageConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.article.pojos.ApArticleEnable;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.*;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.model.wemedia.vo.WmNewsListVo;
import com.heima.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmAutoScanService;
import com.heima.wemedia.service.WmNewsService;
import com.heima.wemedia.service.WmScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Service
@Slf4j
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Autowired
    private WmAutoScanService wmAutoScanService;

    @Autowired
    private WmScheduleService wmScheduleService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public ResponseResult findOne(Integer id) {
        if (id == null || id <= 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        WmNews wmNews = lambdaQuery()
                .eq(WmNews::getId, id)
                .one();
        return ResponseResult.okResult(wmNews);
    }

    @Override
    public ResponseResult findList(WmNewsPageReqDto dto) {
        dto.checkParam();

        LambdaQueryWrapper<WmNews> wrapper = new LambdaQueryWrapper<>();

        if (dto.getStatus() != null) {
            wrapper.eq(WmNews::getStatus, dto.getStatus());
        }

        if (StringUtils.isNotBlank(dto.getKeyword())) {
            wrapper.like(WmNews::getTitle, dto.getKeyword());
        }

        if (dto.getChannelId() != null) {
            wrapper.eq(WmNews::getChannelId, dto.getChannelId());
        }

        if (dto.getBeginPubDate() != null && dto.getEndPubDate() != null) {
            wrapper.between(WmNews::getPublishTime, dto.getBeginPubDate(), dto.getEndPubDate());
        }

        wrapper.eq(WmNews::getUserId, WmThreadLocalUtil.getUser().getId());
        wrapper.orderByDesc(WmNews::getCreatedTime);

        Page<WmNews> page = page(new Page<>(dto.getPage(), dto.getSize()), wrapper);

        List<WmNewsListVo> voList = page.getRecords().stream().map(item -> {
            WmNewsListVo vo = new WmNewsListVo();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());

        PageResponseResult result = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        result.setData(voList);

        return result;
    }

    @Override
    public ResponseResult submitNews(WmNewsDto dto) {
        if (dto == null || dto.getType() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        String content = dto.getContent();
        if (StringUtils.isEmpty(content)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(dto, wmNews);

        List<String> contentUrlList = parseImgUrlList(content);
        List<String> coverUrlList = getCoverUrlList(dto, contentUrlList);

        if (!CollectionUtils.isEmpty(coverUrlList)) {
            wmNews.setImages(StringUtils.join(coverUrlList, ","));
            if (coverUrlList.size() == 3) {
                wmNews.setType(WeMediaConstants.WM_NEWS_MANY_IMAGE);
            } else if (coverUrlList.size() == 1) {
                wmNews.setType(WeMediaConstants.WM_NEWS_SINGLE_IMAGE);
            } else {
                wmNews.setType(WeMediaConstants.WM_NEWS_NONE_IMAGE);
            }
        } else {
            wmNews.setType(WeMediaConstants.WM_NEWS_NONE_IMAGE);
        }

        List<WmMaterial> contentMaterialList = getMaterialList(contentUrlList);
        List<WmMaterial> coverMaterialList = getMaterialList(coverUrlList);

        wmNews.setUserId(WmThreadLocalUtil.getUser().getId());
        Date now = new Date();
        wmNews.setCreatedTime(now);
        wmNews.setSubmitedTime(now);
        wmNews.setEnable((short) 1);

        if (wmNews.getId() == null) {
            wmNews.setReason("审核中");
            save(wmNews);
        } else {
            LambdaQueryWrapper<WmNewsMaterial> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WmNewsMaterial::getNewsId, wmNews.getId());
            wmNewsMaterialMapper.delete(wrapper);
            updateById(wmNews);
        }

        if (!contentMaterialList.isEmpty()) {
            saveRelation(contentMaterialList, wmNews, WeMediaConstants.WM_CONTENT_REFERENCE);
        }

        if (!coverMaterialList.isEmpty()) {
            saveRelation(coverMaterialList, wmNews, WeMediaConstants.WM_COVER_REFERENCE);
        }

//        wmAutoScanService.autoScanWmNews(wmNews.getId());
        wmScheduleService.addNewsToTask(wmNews.getId(), dto.getPublishTime());

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult downOrUp(WmNewsDto dto) {
        if (dto == null || dto.getId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "参数错误");
        }

        WmNews wmNews = getById(dto.getId());
        if (wmNews == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "文章不存在");
        }

        if (WmNews.Status.PUBLISHED.getCode() != wmNews.getStatus()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "文章未上架");
        }

        Short enable = dto.getEnable();
        if (enable == null || (enable != 0 && enable != 1)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "参数错误");
        }

        lambdaUpdate().set(WmNews::getEnable, enable)
                .eq(WmNews::getId, dto.getId())
                .update();

        if (wmNews.getArticleId() != null) {
            ApArticleEnable apArticleEnable = new ApArticleEnable();
            apArticleEnable.setArticleId(wmNews.getArticleId());
            apArticleEnable.setEnable(enable);
            kafkaTemplate.send(WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC, JSON.toJSONString(apArticleEnable));
            log.info("kafka send:{}", JSON.toJSONString(apArticleEnable));
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    private List<String> getCoverUrlList(WmNewsDto dto, List<String> contentUrlList) {
        Short dtoType = dto.getType();
        List<String> dtoCoverImages = dto.getImages();

        List<String> coverUrlList = null;
        if (Objects.equals(dtoType, WeMediaConstants.WM_NEWS_TYPE_AUTO)) {
            if (contentUrlList.size() >= 3) {
                coverUrlList = contentUrlList.stream().limit(3).collect(Collectors.toList());
            } else if (contentUrlList.size() >= 1) {
                coverUrlList = contentUrlList.stream().limit(1).collect(Collectors.toList());
            }

        } else if (Objects.equals(dtoType, WeMediaConstants.WM_NEWS_SINGLE_IMAGE)) {
            if (CollectionUtils.isEmpty(dtoCoverImages) || dtoCoverImages.size() != 1) {
                throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
            }
            coverUrlList = dtoCoverImages;

        } else if (Objects.equals(dtoType, WeMediaConstants.WM_NEWS_MANY_IMAGE)) {
            if (CollectionUtils.isEmpty(dtoCoverImages) || dtoCoverImages.size() != 3) {
                throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);

            }
            coverUrlList = dtoCoverImages;
        }

        return coverUrlList;
    }

    private List<String> parseImgUrlList(String content) {
        List<Map> maps = JSON.parseArray(content, Map.class);
        List<String> urlList = new ArrayList<>();
        for (Map map : maps) {
            if (Objects.equals(map.get("type"), "image")) {
                String url = (String) map.get("value");
                urlList.add(url);
            }
        }
        return urlList;
    }

    @Nonnull
    private List<WmMaterial> getMaterialList(List<String> urlList) {
        if (CollectionUtils.isEmpty(urlList)) {
            return new ArrayList<>();
        }
//        List<WmMaterial> list = wmMaterialMapper.getValidList(urlList);
        LambdaQueryWrapper<WmMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(WmMaterial::getUrl, urlList);
        List<WmMaterial> list = wmMaterialMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(list) || list.size() != urlList.size()) {
            throw new CustomException(AppHttpCodeEnum.MATERIAL_REFERENCE_FAIL);
        }
        return list;
    }


    private void saveRelation(List<WmMaterial> materialList, WmNews wmNews, Short type) {
        List<Integer> idList = materialList.stream().map(WmMaterial::getId).collect(Collectors.toList());
        wmNewsMaterialMapper.saveRelations(idList, wmNews.getId(), type);

    }

    @Override
    public IPage<WmNews> pageList(WmNewsAdminPageDto dto) {

        LambdaQueryWrapper<WmNews> query = Wrappers.lambdaQuery();

        if (StringUtils.isNotBlank(dto.getTitle())) {
            query.and(w -> w.like(WmNews::getTitle, dto.getTitle()))
                    .or().like(WmNews::getContent, dto.getTitle());
        }

        if (dto.getStatus() != null) {
            query.eq(WmNews::getStatus, dto.getStatus());
        }

        query.eq(WmNews::getEnable, 1)
                .orderByDesc(WmNews::getCreatedTime);

        IPage<WmNews> iPage = new Page<>(dto.getPage(), dto.getSize());
        IPage<WmNews> pageResult = page(iPage, query);

        return pageResult;
    }

    @Override
    public void authFail(WmNewsAuthFailDto dto) {
        WmNews wmNews = getById(dto.getId());
        if (wmNews == null){
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        if (dto.getMsg() != null){
            wmNews.setReason(dto.getMsg());
        }

        wmNews.setStatus(WmNews.Status.FAIL.getCode());

        updateById(wmNews);

    }

    @Override
    public void authPass(WmNewsAuthPassDto dto) {
        WmNews wmNews = getById(dto.getId());
        if (wmNews == null){
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        wmNews.setStatus(WmNews.Status.SUCCESS.getCode());

        updateById(wmNews);
    }


}
