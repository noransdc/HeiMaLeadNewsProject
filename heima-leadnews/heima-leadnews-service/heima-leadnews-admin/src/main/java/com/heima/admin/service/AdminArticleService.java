package com.heima.admin.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.heima.model.articlecore.dto.AdminArticlePageDto;
import com.heima.model.articlecore.dto.AuthorArticlePageDto;
import com.heima.model.articlecore.entity.Article;
import com.heima.model.articlecore.vo.AdminArticleVo;
import com.heima.model.common.dtos.PageResponseResult;

import java.util.List;



public interface AdminArticleService {


    PageResponseResult<List<AdminArticleVo>> pageForAdmin(AdminArticlePageDto dto);

    AdminArticleVo forAdmin(Long id);


}
