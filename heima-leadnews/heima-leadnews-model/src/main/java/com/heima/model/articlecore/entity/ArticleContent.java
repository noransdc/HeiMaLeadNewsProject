package com.heima.model.articlecore.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("article_content")
public class ArticleContent {


    @TableId(value = "article_id", type = IdType.INPUT)
    private Long articleId;

    @TableField("content")
    private String content;


}
