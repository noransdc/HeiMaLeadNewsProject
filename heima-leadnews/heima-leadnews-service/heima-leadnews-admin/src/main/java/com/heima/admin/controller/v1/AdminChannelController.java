package com.heima.admin.controller.v1;


import com.heima.apis.articlecore.ArticleChannelClient;
import com.heima.model.articlecore.dto.ArticleChannelAddDto;
import com.heima.model.articlecore.dto.ArticleChannelPageDto;
import com.heima.model.articlecore.dto.ArticleChannelUpdateDto;
import com.heima.model.articlecore.vo.AdminChannelVo;
import com.heima.model.articlecore.vo.AuthorChannelVo;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/wemedia/api/v1/channel")
public class AdminChannelController {

    @Autowired
    private ArticleChannelClient channelClient;

    @PostMapping("/list")
    public PageResponseResult<List<AdminChannelVo>> pageForAdmin(@RequestBody ArticleChannelPageDto dto){
        return channelClient.pageForAdmin(dto);
    }

    @PostMapping("/save")
    public ResponseResult add(@RequestBody ArticleChannelAddDto dto){
        channelClient.add(dto);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @PostMapping("/update")
    public ResponseResult update(@RequestBody ArticleChannelUpdateDto dto){
        channelClient.update(dto);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @GetMapping("/del/{id}")
    public ResponseResult delete(@PathVariable Long id){
        channelClient.delete(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }




}
