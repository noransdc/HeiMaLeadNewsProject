package com.heima.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.user.dtos.ApCollectionDto;
import com.heima.model.user.pojos.ApUserCollection;
import com.heima.user.mapper.ApUserCollectionMapper;


public interface ApUserCollectionService extends IService<ApUserCollection> {


    void collectArticle(ApCollectionDto dto);



}
