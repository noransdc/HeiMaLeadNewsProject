package com.heima.article.core.controller.v1;


import com.heima.article.core.service.ArticleSensitiveService;
import com.heima.model.articlecore.dto.SensitiveAddDto;
import com.heima.model.articlecore.dto.SensitivePageDto;
import com.heima.model.articlecore.dto.SensitiveUpdateDto;
import com.heima.model.articlecore.vo.SensitiveVo;
import com.heima.model.common.dtos.PageResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/internal/sensitive")
public class SensitiveInternalController {

    @Autowired
    private ArticleSensitiveService articleSensitiveService;

    @PostMapping("/page")
    public PageResponseResult<List<SensitiveVo>> pageList(@RequestBody SensitivePageDto dto){
        return articleSensitiveService.pageList(dto);
    }

    @PostMapping
    public void add(@RequestBody SensitiveAddDto dto){
        articleSensitiveService.add(dto);
    }

    @PutMapping
    public void update(@RequestBody SensitiveUpdateDto dto){
        articleSensitiveService.update(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        articleSensitiveService.delete(id);
    }


}
