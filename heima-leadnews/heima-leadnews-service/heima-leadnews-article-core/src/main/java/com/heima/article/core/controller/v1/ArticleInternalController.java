package com.heima.article.core.controller.v1;


import com.heima.article.core.service.ArticleChannelService;
import com.heima.article.core.service.ArticleService;
import com.heima.model.articlecore.dto.ArticleSubmitDto;
import com.heima.model.articlecore.entity.ArticleChannel;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/internal/article")
public class ArticleInternalController {


    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleChannelService articleChannelService;

    @PostMapping("/add")
    public void submit(@RequestBody ArticleSubmitDto dto){
        articleService.submit(dto);
    }

    @GetMapping("/channel/list")
    public List<ArticleChannel> getChannelList(){
        return articleChannelService.getChannelList();
    }

}
