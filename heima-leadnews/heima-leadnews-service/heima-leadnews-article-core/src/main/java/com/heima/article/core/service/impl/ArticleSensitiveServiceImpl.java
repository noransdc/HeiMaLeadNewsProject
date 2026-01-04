package com.heima.article.core.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.core.mapper.ArticleSensitiveMapper;
import com.heima.article.core.service.ArticleSensitiveService;
import com.heima.model.articlecore.entity.ArticleSensitive;
import com.heima.utils.common.SensitiveWordUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class ArticleSensitiveServiceImpl extends ServiceImpl<ArticleSensitiveMapper, ArticleSensitive> implements ArticleSensitiveService {


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
        List<String> wordList= new ArrayList<>();
        for (ArticleSensitive sensitive : list) {
            wordList.add(sensitive.getName());
        }

        SensitiveWordUtil.initMap(wordList);

        return SensitiveWordUtil.matchWords(text);
    }


}
