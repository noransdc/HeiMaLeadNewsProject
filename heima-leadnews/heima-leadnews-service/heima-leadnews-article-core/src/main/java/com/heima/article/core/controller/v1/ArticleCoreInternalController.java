package com.heima.article.core.controller.v1;


import com.heima.article.core.service.ArticleService;
import com.heima.article.core.service.HotArticleRankService;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.articlecore.dto.AdminArticlePageDto;
import com.heima.model.articlecore.dto.ArticleAuthFailDto;
import com.heima.model.articlecore.dto.ArticleSubmitDto;
import com.heima.model.articlecore.dto.AuthorArticlePageDto;
import com.heima.model.articlecore.vo.AdminArticleListVo;
import com.heima.model.articlecore.vo.AuthorArticleDetailVo;
import com.heima.model.articlecore.vo.AuthorArticleListVo;
import com.heima.model.articlecore.vo.FrontArticleListVo;
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

    @Autowired
    private HotArticleRankService hotArticleRankService;


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

    @GetMapping("/author/{id}")
    public AuthorArticleDetailVo detailForAuthor(@PathVariable Long id){
        return articleService.detailForAuthor(id);
    }

    @PostMapping("/admin/page")
    public PageResponseResult<List<AdminArticleListVo>> pageForAdmin(@RequestBody AdminArticlePageDto dto){
        return articleService.pageAllArticles(dto);
    }

    @GetMapping("/admin/{id}")
    public AuthorArticleDetailVo detailForAdmin(@PathVariable Long id){
        return articleService.detailForAuthor(id);
    }

    @PostMapping("/admin/auth_fail")
    public void manualAuditReject(@RequestBody ArticleAuthFailDto dto){
        articleService.manualAuditReject(dto);
    }

    @PostMapping("/admin/auth_pass/{id}")
    public void manualAuditPass(@PathVariable Long id){
        articleService.manualAuditPass(id);
    }

    @PostMapping("/front/hot")
    public List<FrontArticleListVo> forFrontHot(@RequestBody ArticleHomeDto dto){
        return hotArticleRankService.getHotArticlesByChannel(dto);
    }


}
