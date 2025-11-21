package com.heima.wemedia.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.vo.WmNewsLisVo;
import com.heima.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.WmNewsService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {


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


}
