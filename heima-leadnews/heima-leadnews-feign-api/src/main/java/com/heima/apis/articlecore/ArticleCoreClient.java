package com.heima.apis.articlecore;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.heima.model.articlecore.dto.ArticleDetailDto;
import com.heima.model.articlecore.dto.ArticlePageDto;
import com.heima.model.articlecore.dto.ArticleSubmitDto;
import com.heima.model.articlecore.entity.Article;
import com.heima.model.articlecore.entity.ArticleChannel;
import com.heima.model.articlecore.vo.ArticleVo;
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

    @PostMapping("/add")
    void submit(@RequestBody ArticleSubmitDto dto);

    @PostMapping("/pending-audit-ids")
    List<ArticleAuditCompensateDto> getArticleAuditCompensateList(@RequestBody PageRequestDto dto);

    @PostMapping("/audit/{articleId}")
    void postAudit(@PathVariable Long articleId);

    @PostMapping("/publish/{articleId}")
    void postPublish(@PathVariable Long articleId);

    @PostMapping("/page/list")
    PageResponseResult<List<ArticleVo>> getPageList(@RequestBody ArticlePageDto dto);

    @GetMapping("/detail/{id}")
    ArticleVo getArticleDetail(@PathVariable Long id);


}
