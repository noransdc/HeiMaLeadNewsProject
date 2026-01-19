package com.heima.article.controller.v1;


import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.articlecore.vo.AuthorArticleListVo;
import com.heima.model.common.dtos.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/article")
@Api(value = "文章服务",tags = "文章服务")
public class ArticleHomeController {

    @Autowired
    private ApArticleService apArticleService;

    @ApiOperation("获取最新文章列表")
    @PostMapping({"/load", "/load/"})
    public ResponseResult load(@RequestBody ArticleHomeDto dto){
        List<AuthorArticleListVo> hotList = apArticleService.getHotList(dto);
        return ResponseResult.okResult(hotList);
    }

    @PostMapping("/loadmore")
    public ResponseResult loadMore(@RequestBody ArticleHomeDto dto){
        return apArticleService.load(dto, ArticleConstants.LOADTYPE_LOAD_MORE);
    }

    @PostMapping("/loadnew")
    public ResponseResult loadNew(@RequestBody ArticleHomeDto dto){
        return apArticleService.load(dto, ArticleConstants.LOADTYPE_LOAD_NEW);
    }

}
