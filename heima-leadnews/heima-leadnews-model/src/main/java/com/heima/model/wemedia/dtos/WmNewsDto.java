package com.heima.model.wemedia.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class WmNewsDto {
    
    private Long id;
     /**
     * 标题
     */
    private String title;
     /**
     * 频道id
     */
    private Long channelId;
     /**
     * 标签
     */
    private String labels;
     /**
     * 发布时间
     */
    private LocalDateTime publishTime;
     /**
     * 文章内容
     */
    private String content;
     /**
     * 文章封面类型  0无图  1单图  3多图  -1自动
     */
    private Integer type;

     /**
     * 状态 提交为1  草稿为0
     */
    private Integer status;
     
     /**
     * 封面图片列表 多张图以逗号隔开
     */
    private List<String> images;

    //0下架， 1上架
    private Integer enable;


}