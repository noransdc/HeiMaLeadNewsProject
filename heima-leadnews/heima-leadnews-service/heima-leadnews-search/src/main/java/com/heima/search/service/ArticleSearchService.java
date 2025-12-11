package com.heima.search.service;


import com.heima.model.search.dto.UserSearchDto;
import com.heima.model.common.dtos.ResponseResult;

import java.io.IOException;


public interface ArticleSearchService {


    ResponseResult search(UserSearchDto dto)  throws IOException;

    void insert(String keywords, Integer userId);

}
