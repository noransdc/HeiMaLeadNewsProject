package com.heima.apis.articlecore;


import com.heima.model.articlecore.dto.SensitiveAddDto;
import com.heima.model.articlecore.dto.SensitivePageDto;
import com.heima.model.articlecore.dto.SensitiveUpdateDto;
import com.heima.model.articlecore.vo.SensitiveVo;
import com.heima.model.common.dtos.PageResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@FeignClient(name = "leadnews-article-core",
        path = "/internal/sensitive",
        contextId = "articleSensitiveClient")
public interface ArticleSensitiveClient {


    @PostMapping("/page")
    PageResponseResult<List<SensitiveVo>> pageList(@RequestBody SensitivePageDto dto);

    @PostMapping
    void add(@RequestBody SensitiveAddDto dto);

    @PutMapping
    void update(@RequestBody SensitiveUpdateDto dto);

    @DeleteMapping("/{id}")
    void delete(@PathVariable Long id);


}
