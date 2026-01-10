package com.heima.article.core.controller.v1;


import com.heima.article.core.convert.ChannelConvert;
import com.heima.article.core.service.ArticleChannelService;
import com.heima.model.articlecore.dto.ArticleChannelAddDto;
import com.heima.model.articlecore.dto.ArticleChannelPageDto;
import com.heima.model.articlecore.dto.ArticleChannelUpdateDto;
import com.heima.model.articlecore.entity.ArticleChannel;
import com.heima.model.articlecore.vo.AdminChannelVo;
import com.heima.model.articlecore.vo.AuthorChannelVo;
import com.heima.model.common.dtos.PageResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/internal/channel")
public class ArticleChannelInternalController {

    @Autowired
    private ArticleChannelService articleChannelService;

    @GetMapping("/author")
    public List<AuthorChannelVo> listForAuthor(){
        List<ArticleChannel> channelList = articleChannelService.listEnable();
        return ChannelConvert.toAuthorVoList(channelList);
    }

    @PostMapping("/admin/page")
    public PageResponseResult<List<AdminChannelVo>> pageForAdmin(@RequestBody ArticleChannelPageDto dto){
        PageResponseResult<List<ArticleChannel>> pageRsp = articleChannelService.listPage(dto);
        return ChannelConvert.toAdminVoPage(pageRsp);
    }

    @PostMapping("/admin")
    public void add(@RequestBody ArticleChannelAddDto dto){
        articleChannelService.add(dto);
    }

    @PutMapping("/admin")
    public void update(@RequestBody ArticleChannelUpdateDto dto){
        articleChannelService.update(dto);
    }

    @DeleteMapping("/admin/{id}")
    public void delete(@PathVariable Long id){
        articleChannelService.delete(id);
    }


}
