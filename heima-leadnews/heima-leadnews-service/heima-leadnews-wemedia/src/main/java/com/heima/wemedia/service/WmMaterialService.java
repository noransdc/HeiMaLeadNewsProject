package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import org.springframework.web.multipart.MultipartFile;


public interface WmMaterialService extends IService<WmMaterial> {

    WmMaterial uploadPicture(MultipartFile multipartFile);

    ResponseResult findList(WmMaterialDto dto);

    ResponseResult addCollection(Integer id);

    ResponseResult cancelCollection(Integer id);

    ResponseResult delete(Integer id);



}
