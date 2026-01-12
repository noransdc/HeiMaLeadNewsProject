package com.heima.article.core.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.core.mapper.ArticleSensitiveMapper;
import com.heima.article.core.service.ArticleSensitiveService;
import com.heima.common.exception.CustomException;
import com.heima.model.articlecore.dto.SensitiveAddDto;
import com.heima.model.articlecore.dto.SensitivePageDto;
import com.heima.model.articlecore.dto.SensitiveUpdateDto;
import com.heima.model.articlecore.entity.ArticleSensitive;
import com.heima.model.articlecore.vo.SensitiveVo;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.common.SensitiveWordUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class ArticleSensitiveServiceImpl extends ServiceImpl<ArticleSensitiveMapper, ArticleSensitive>
        implements ArticleSensitiveService {


    @Override
    public List<ArticleSensitive> getDataList() {
        List<ArticleSensitive> list = lambdaQuery()
                .eq(ArticleSensitive::getIsDelete, 0)
                .list();
        return list;
    }

    @Override
    public Map<String, Integer> scan(String text) {

        List<ArticleSensitive> list = getDataList();
        List<String> wordList = new ArrayList<>();
        for (ArticleSensitive sensitive : list) {
            wordList.add(sensitive.getName());
        }

        SensitiveWordUtil.initMap(wordList);

        return SensitiveWordUtil.matchWords(text);
    }

    @Override
    public PageResponseResult<List<SensitiveVo>> pageList(SensitivePageDto dto) {
        LambdaQueryWrapper<ArticleSensitive> query = Wrappers.lambdaQuery();
        query.eq(ArticleSensitive::getIsDelete, 0)
                .orderByDesc(ArticleSensitive::getCreateTime);

        Page<ArticleSensitive> pageRsp = page(new Page<>(dto.getPage(), dto.getSize()), query);

        List<SensitiveVo> voList = new ArrayList<>();
        for (ArticleSensitive record : pageRsp.getRecords()) {
            voList.add(new SensitiveVo(record.getId(), record.getName(), record.getCreateTime()));
        }
        PageResponseResult<List<SensitiveVo>> result = new PageResponseResult<>(dto.getPage(), dto.getSize(), (int)pageRsp.getTotal());
        result.setData(voList);

        return result;
    }

    @Override
    public void add(SensitiveAddDto dto) {
        if (StringUtils.isBlank(dto.getSensitives())){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        ArticleSensitive articleSensitive = new ArticleSensitive();
        articleSensitive.setName(dto.getSensitives());

        save(articleSensitive);
    }

    @Override
    public void update(SensitiveUpdateDto dto) {
        if (dto.getId() == null || StringUtils.isBlank(dto.getSensitives())){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }
        ArticleSensitive articleSensitive = getById(dto.getId());

        if (articleSensitive == null){
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        articleSensitive.setName(dto.getSensitives());

        updateById(articleSensitive);
    }

    @Override
    public void delete(Long id) {
        if (id == null){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        ArticleSensitive articleSensitive = getById(id);

        if (articleSensitive == null){
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        articleSensitive.setIsDelete(1);
        articleSensitive.setDeleteTime(LocalDateTime.now());

        updateById(articleSensitive);
    }


}
