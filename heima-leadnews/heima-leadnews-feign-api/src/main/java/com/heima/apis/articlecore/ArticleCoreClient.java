package com.heima.apis.articlecore;


import com.heima.model.articlecore.dto.ArticleSubmitDto;
import com.heima.model.articlecore.entity.ArticleChannel;
import com.heima.model.common.dtos.PageRequestDto;
import com.heima.model.schedule.dto.ArticleAuditCompensateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(value = "leadnews-article-core")
public interface ArticleCoreClient {

    @PostMapping("/internal/article/add")
    void submit(@RequestBody ArticleSubmitDto dto);

    @GetMapping("/internal/article/channel/list")
    List<ArticleChannel> getChannelList();

    @PostMapping("/internal/article/pending-audit-ids")
    List<ArticleAuditCompensateDto> getArticleAuditCompensateList(@RequestBody PageRequestDto dto);

    @PostMapping("/internal/article/audit/{articleId}")
    void postAudit(@PathVariable Long articleId);

}
