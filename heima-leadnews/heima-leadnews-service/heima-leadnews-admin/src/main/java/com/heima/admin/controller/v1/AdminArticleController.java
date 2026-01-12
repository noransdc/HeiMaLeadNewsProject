package com.heima.admin.controller.v1;


import com.heima.admin.service.AdminArticleService;
import com.heima.model.articlecore.dto.AdminArticlePageDto;
import com.heima.model.articlecore.vo.AdminArticleListVo;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.articlecore.dto.ArticleAuthFailDto;
import com.heima.model.articlecore.dto.ArticleAuthPassDto;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("wemedia/api/v1/news")
public class AdminArticleController {

    @Autowired
    private AdminArticleService adminArticleService;

    @PostMapping("/list_vo")
    public PageResponseResult<List<AdminArticleListVo>> listForAdmin(@RequestBody AdminArticlePageDto dto){
        return adminArticleService.pageForAdmin(dto);
    }

    @GetMapping("/one_vo/{id}")
    public ResponseResult detailForAdmin(@PathVariable Long id){
        return ResponseResult.okResult(adminArticleService.detailForAdmin(id));
    }

//    @PostMapping("/down_or_up")
//    public ResponseResult downOrUp(@RequestBody WmNewsDto dto){
//        return null;
//    }
//
//    @GetMapping("/one_vo/{id}")
//    public ResponseResult getOne(@PathVariable Integer id){
//        return null;
//    }

    @PostMapping("/auth_fail")
    public ResponseResult manualAuditReject(@RequestBody ArticleAuthFailDto dto){
        adminArticleService.manualAuditReject(dto);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @PostMapping("/auth_pass")
    public ResponseResult manualAuditPass(@RequestBody ArticleAuthPassDto dto){
        adminArticleService.manualAuditPass(dto.getId());
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }


}
