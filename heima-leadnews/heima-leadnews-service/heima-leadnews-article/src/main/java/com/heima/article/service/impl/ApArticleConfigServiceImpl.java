package com.heima.article.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.service.ApArticleConfigService;
import com.heima.model.article.pojos.ApArticleConfig;
import org.springframework.stereotype.Service;


@Service
public class ApArticleConfigServiceImpl extends ServiceImpl<ApArticleConfigMapper, ApArticleConfig> implements ApArticleConfigService {


    @Override
    public void downOrUp(Long articleId, Short enable) {
        if (articleId == null || enable == null){
            return;
        }
        short isDown = (short) (1 - enable);

        lambdaUpdate().set(ApArticleConfig::getIsDown, isDown)
                .eq(ApArticleConfig::getArticleId, articleId)
                .update();

    }


}
