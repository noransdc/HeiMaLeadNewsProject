package com.heima.article.core.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.articlecore.entity.ArticleSensitive;

import java.util.List;
import java.util.Map;


public interface ArticleSensitiveService extends IService<ArticleSensitive> {


    List<ArticleSensitive> getDataList();

    Map<String, Integer> scan(String text);

}
