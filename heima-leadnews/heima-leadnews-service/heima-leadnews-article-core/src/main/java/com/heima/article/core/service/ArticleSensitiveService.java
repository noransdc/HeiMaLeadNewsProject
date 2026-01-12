package com.heima.article.core.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.articlecore.dto.SensitiveAddDto;
import com.heima.model.articlecore.dto.SensitivePageDto;
import com.heima.model.articlecore.dto.SensitiveUpdateDto;
import com.heima.model.articlecore.entity.ArticleSensitive;
import com.heima.model.articlecore.vo.SensitiveVo;
import com.heima.model.common.dtos.PageResponseResult;

import java.util.List;
import java.util.Map;


public interface ArticleSensitiveService extends IService<ArticleSensitive> {


    List<ArticleSensitive> getDataList();

    Map<String, Integer> scan(String text);

    PageResponseResult<List<SensitiveVo>> pageList(SensitivePageDto dto);

    void add(SensitiveAddDto dto);

    void update(SensitiveUpdateDto dto);

    void delete(Long id);

}
