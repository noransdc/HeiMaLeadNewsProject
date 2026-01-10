package com.heima.article.core.controller.v1;


import com.heima.article.core.service.ArticleService;
import com.heima.model.articlecore.dto.*;
import com.heima.model.articlecore.vo.ArticleVo;
import com.heima.model.common.dtos.PageRequestDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.schedule.dto.ArticleAuditCompensateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/internal/article")
public class ArticleCoreInternalController {


    @Autowired
    private ArticleService articleService;


    @PostMapping("/add")
    public void submit(@RequestBody ArticleSubmitDto dto){
        articleService.submit(dto);
    }

    @PostMapping("/pending-audit-ids")
    public List<ArticleAuditCompensateDto> getArticleAuditCompensateList(@RequestBody PageRequestDto dto){
        return articleService.getArticleAuditCompensateList(dto);
    }

    @PostMapping("/audit/{articleId}")
    public void audit(@PathVariable Long articleId){
        articleService.audit(articleId);
    }

    @PostMapping("/publish/{articleId}")
    public void publish(@PathVariable Long articleId){
        articleService.publish(articleId);
    }

    @PostMapping("/page/list")
    public PageResponseResult<List<ArticleVo>> getPageList(@RequestBody ArticlePageDto dto){
        return articleService.getPageList(dto);
    }

    @GetMapping("/detail/{id}")
    public ArticleVo getArticleVo(@PathVariable Long id){
        return articleService.getArticleVo(id);
    }


}
