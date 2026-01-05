package com.heima.model.schedule.dto;


import com.heima.model.articlecore.dto.ArticlePublishDto;
import lombok.Data;

import java.util.List;

@Data
public class ArticleTaskDto {


    private List<ArticlePublishDto> list;


}
