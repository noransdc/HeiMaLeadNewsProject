package com.heima.admin.controller.v1;


import com.heima.admin.service.AdminArticleService;
import com.heima.model.articlecore.dto.AdminArticlePageDto;
import com.heima.model.articlecore.dto.AuthorArticlePageDto;
import com.heima.model.articlecore.vo.AdminArticleVo;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("wemedia/api/v1/news")
public class AdminArticleController {

    @Autowired
    private AdminArticleService adminArticleService;

    @PostMapping("/list_vo")
    public PageResponseResult<List<AdminArticleVo>> listForAdmin(@RequestBody AdminArticlePageDto dto){
        return adminArticleService.pageForAdmin(dto);
    }

    @GetMapping("/one/{id}")
    public ResponseResult forAdmin(@PathVariable Long id){
        return ResponseResult.okResult(adminArticleService.forAdmin(id));
    }


}
