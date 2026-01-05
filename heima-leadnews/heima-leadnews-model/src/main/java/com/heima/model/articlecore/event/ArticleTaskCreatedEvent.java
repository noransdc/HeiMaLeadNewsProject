package com.heima.model.articlecore.event;

import com.heima.model.articlecore.entity.Article;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ArticleTaskCreatedEvent {

    private Article article;


}
