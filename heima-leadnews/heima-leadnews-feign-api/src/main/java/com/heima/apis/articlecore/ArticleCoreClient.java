package com.heima.apis.articlecore;


import com.heima.model.articlecore.dto.AdminArticlePageDto;
import com.heima.model.articlecore.dto.AuthorArticlePageDto;
import com.heima.model.articlecore.dto.ArticleSubmitDto;
import com.heima.model.articlecore.vo.AdminArticleListVo;
import com.heima.model.articlecore.vo.AuthorArticleListVo;
import com.heima.model.common.dtos.PageRequestDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.schedule.dto.ArticleAuditCompensateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name = "leadnews-article-core",
        path = "/internal/article",
        contextId = "articleCoreClient"
)
public interface ArticleCoreClient {

    @PostMapping("/author")
    void submit(@RequestBody ArticleSubmitDto dto);

    @PostMapping("/pending-audit-ids")
    List<ArticleAuditCompensateDto> getArticleAuditCompensateList(@RequestBody PageRequestDto dto);

    @PostMapping("/audit/{articleId}")
    void postAudit(@PathVariable Long articleId);

    @PostMapping("/publish/{articleId}")
    void postPublish(@PathVariable Long articleId);

    @PostMapping("/author/page")
    PageResponseResult<List<AuthorArticleListVo>> pageForAuthor(@RequestBody AuthorArticlePageDto dto);

    @GetMapping("/detail/{id}")
    AuthorArticleListVo getArticleDetail(@PathVariable Long id);

    @PostMapping("/admin/page")
    PageResponseResult<List<AdminArticleListVo>> pageForAdmin(@RequestBody AdminArticlePageDto dto);

    @GetMapping("/admin/{id}")
    AdminArticleListVo forAdmin(@PathVariable Long id);

}
