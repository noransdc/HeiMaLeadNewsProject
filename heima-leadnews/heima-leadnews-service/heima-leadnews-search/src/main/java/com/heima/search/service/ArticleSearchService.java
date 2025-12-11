package com.heima.search.service;


import com.heima.model.search.dto.DeleteHistoryDto;
import com.heima.model.search.dto.LoadHistoryDto;
import com.heima.model.search.dto.UserSearchDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;


public interface ArticleSearchService {


    ResponseResult search(UserSearchDto dto)  throws IOException;

    void insert(String keywords, Integer userId);

    ResponseResult load(LoadHistoryDto dto);

    ResponseResult delete( DeleteHistoryDto dto);

    ResponseResult searchAssociate( UserSearchDto dto);


}
