package com.heima.wemedia.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.WeMediaConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.model.wemedia.vo.WmNewsLisVo;
import com.heima.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmNewsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Override
    public ResponseResult findList(WmNewsPageReqDto dto) {
        dto.checkParam();

        LambdaQueryWrapper<WmNews> wrapper = new LambdaQueryWrapper<>();

        if (dto.getStatus() != null){
            wrapper.eq(WmNews::getStatus, dto.getStatus());
        }

        if (StringUtils.isNotBlank(dto.getKeyword())){
            wrapper.like(WmNews::getTitle, dto.getKeyword());
        }

        if (dto.getChannelId() != null){
            wrapper.eq(WmNews::getChannelId, dto.getChannelId());
        }

        if (dto.getBeginPubDate() != null && dto.getEndPubDate() != null){
            wrapper.between(WmNews::getPublishTime, dto.getBeginPubDate(), dto.getEndPubDate());
        }

        wrapper.eq(WmNews::getUserId, WmThreadLocalUtil.getUser().getId());

        Page<WmNews> page = page(new Page<>(dto.getPage(), dto.getSize()), wrapper);

        List<WmNewsLisVo> voList = page.getRecords().stream().map(item->{
            WmNewsLisVo vo = new WmNewsLisVo();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());

        PageResponseResult result = new PageResponseResult(dto.getPage(), dto.getSize(), (int)page.getTotal());
        result.setData(voList);

        return result;
    }

    @Override
    public ResponseResult submitNews(WmNewsDto dto) {
        if (dto == null || dto.getType() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        String content = dto.getContent();
        if (StringUtils.isEmpty(content)){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(dto, wmNews);

        List<String> contentUrlList = parseImgUrlList(content);
        List<String> coverUrlList = getCoverUrlList(dto, contentUrlList);

        if (!CollectionUtils.isEmpty(coverUrlList)){
            wmNews.setImages(StringUtils.join(coverUrlList, ","));
            if (coverUrlList.size() == 3){
                wmNews.setType(WeMediaConstants.WM_NEWS_MANY_IMAGE);
            } else if (coverUrlList.size() == 1){
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
        wmNews.setEnable((short)1);

        if (wmNews.getId() == null){
            wmNews.setReason("审核中");
            save(wmNews);
        } else {
            LambdaQueryWrapper<WmNewsMaterial> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WmNewsMaterial::getNewsId, wmNews.getId());
            wmNewsMaterialMapper.delete(wrapper);
            updateById(wmNews);
        }

        saveRelation(contentMaterialList, wmNews, WeMediaConstants.WM_CONTENT_REFERENCE);

        saveRelation(coverMaterialList, wmNews, WeMediaConstants.WM_COVER_REFERENCE);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    private List<String> getCoverUrlList(WmNewsDto dto, List<String> contentUrlList){
        Short dtoType = dto.getType();
        List<String> dtoCoverImages = dto.getImages();

        List<String> coverUrlList = null;
        if (Objects.equals(dtoType, WeMediaConstants.WM_NEWS_TYPE_AUTO)){
            if (contentUrlList.size() >= 3){
                coverUrlList = contentUrlList.stream().limit(3).collect(Collectors.toList());
            } else if (contentUrlList.size() >= 1){
                coverUrlList = contentUrlList.stream().limit(1).collect(Collectors.toList());
            }

        } else if (Objects.equals(dtoType, WeMediaConstants.WM_NEWS_SINGLE_IMAGE)){
            if (CollectionUtils.isEmpty(dtoCoverImages) || dtoCoverImages.size() != 1){
                throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
            }
            coverUrlList = dtoCoverImages;

        } else if (Objects.equals(dtoType, WeMediaConstants.WM_NEWS_MANY_IMAGE)){
            if (CollectionUtils.isEmpty(dtoCoverImages) || dtoCoverImages.size() != 3){
                throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);

            }
            coverUrlList = dtoCoverImages;
        }

        return coverUrlList;
    }

    private List<String> parseImgUrlList(String content){
        List<Map> maps = JSON.parseArray(content, Map.class);
        List<String> urlList = new ArrayList<>();
        for (Map map : maps) {
            if (Objects.equals(map.get("type"), "image")){
                String url = (String) map.get("value");
                urlList.add(url);
            }
        }
        return urlList;
    }

    @Nonnull
    private List<WmMaterial> getMaterialList(List<String> urlList){
        if (CollectionUtils.isEmpty(urlList)){
            return new ArrayList<>();
        }
//        List<WmMaterial> list = wmMaterialMapper.getValidList(urlList);
        LambdaQueryWrapper<WmMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(WmMaterial::getUrl, urlList);
        List<WmMaterial> list = wmMaterialMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(list) || list.size() != urlList.size()){
            throw new CustomException(AppHttpCodeEnum.MATERIAL_REFERENCE_FAIL);
        }
        return list;
    }


    private void saveRelation(List<WmMaterial> materialList, WmNews wmNews, Short type){
        List<Integer> idList = materialList.stream().map(WmMaterial::getId).collect(Collectors.toList());
        wmNewsMaterialMapper.saveRelations(idList, wmNews.getId(), type);

    }


}
