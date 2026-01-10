package com.heima.apis.articlecore;


import com.heima.model.articlecore.dto.ArticleChannelAddDto;
import com.heima.model.articlecore.dto.ArticleChannelPageDto;
import com.heima.model.articlecore.dto.ArticleChannelUpdateDto;
import com.heima.model.articlecore.entity.ArticleChannel;
import com.heima.model.articlecore.vo.AdminChannelVo;
import com.heima.model.articlecore.vo.AuthorChannelVo;
import com.heima.model.common.dtos.PageResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@FeignClient(name = "leadnews-article-core",
        path = "/internal/channel",
        contextId = "articleChannelClient"
)
public interface ArticleChannelClient {


    @GetMapping("/author")
    List<AuthorChannelVo> listForAuthor();

    @PostMapping("/admin/page")
    PageResponseResult<List<AdminChannelVo>> pageForAdmin(@RequestBody ArticleChannelPageDto dto);

    @PostMapping("/admin")
    void add(@RequestBody ArticleChannelAddDto dto);

    @PutMapping("/admin")
    void update(@RequestBody ArticleChannelUpdateDto dto);

    @DeleteMapping("/admin/{id}")
    void delete(@PathVariable Long id);


}
