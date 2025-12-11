package com.heima.search.controller;


import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dto.DeleteHistoryDto;
import com.heima.model.search.dto.LoadHistoryDto;
import com.heima.search.service.ArticleSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/history")
public class SearchHistoryController {

    @Autowired
    private ArticleSearchService articleSearchService;

    @PostMapping("/load")
    public ResponseResult load(@RequestBody LoadHistoryDto dto){
        return articleSearchService.load(dto);
    }

    @PostMapping("/del")
    public ResponseResult delete(@RequestBody DeleteHistoryDto dto){
        return articleSearchService.delete(dto);
    }

}
