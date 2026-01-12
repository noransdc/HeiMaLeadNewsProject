package com.heima.article.core.controller.v1;


import com.heima.article.core.convert.ArticleConvert;
import com.heima.article.core.service.ArticleService;
import com.heima.model.articlecore.dto.*;
import com.heima.model.articlecore.entity.Article;
import com.heima.model.articlecore.vo.AdminArticleListVo;
import com.heima.model.articlecore.vo.AuthorArticleListVo;
import com.heima.model.common.dtos.PageRequestDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.schedule.dto.ArticleAuditCompensateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/internal/article")
public class ArticleCoreInternalController {


    @Autowired
    private ArticleService articleService;


    @PostMapping("/author")
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

    @PostMapping("/author/page")
    public PageResponseResult<List<AuthorArticleListVo>> pageForAuthor(@RequestBody AuthorArticlePageDto dto){
        return articleService.pageOwnArticles(dto);
    }

    @GetMapping("/detail/{id}")
    public AuthorArticleListVo detailForAuthor(@PathVariable Long id){
        Article article = articleService.getArticle(id);
        return ArticleConvert.toAuthorListVo(article);
    }

    @PostMapping("/admin/page")
    public PageResponseResult<List<AdminArticleListVo>> pageForAdmin(@RequestBody AdminArticlePageDto dto){
        return articleService.pageAllArticles(dto);
    }

    @GetMapping("/admin/{id}")
    public AdminArticleListVo forAdmin(@PathVariable Long id){
        Article article = articleService.getArticle(id);
        return ArticleConvert.toAdminListVo(article);
    }

    @PostMapping("/admin/auth_fail")
    public void manualAuditReject(@RequestBody ArticleAuthFailDto dto){
        articleService.manualAuditReject(dto);
    }

    @PostMapping("/admin/auth_pass/{id}")
    public void manualAuditPass(@PathVariable Long id){
        articleService.manualAuditPass(id);
    }


}
