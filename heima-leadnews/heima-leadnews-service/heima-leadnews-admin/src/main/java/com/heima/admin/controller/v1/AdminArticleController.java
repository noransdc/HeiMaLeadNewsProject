package com.heima.admin.controller.v1;


import com.heima.apis.articlecore.ArticleCoreClient;
import com.heima.model.articlecore.dto.ArticlePageDto;
import com.heima.model.articlecore.vo.ArticleVo;
import com.heima.model.common.dtos.PageResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("wemedia/api/v1/news")
public class AdminArticleController {

    @Autowired
    private ArticleCoreClient articleCoreClient;

    @PostMapping("/list_vo")
    public PageResponseResult<List<ArticleVo>> getPageList(ArticlePageDto dto){
        return articleCoreClient.getPageList(dto);
    }

}
