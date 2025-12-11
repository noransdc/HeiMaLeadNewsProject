package com.heima.search.service;


import com.heima.model.search.dto.ArticleSearchDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;


public interface ArticleSearchService {


    ResponseResult search(@RequestBody ArticleSearchDto dto)  throws IOException;



}
