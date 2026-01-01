package com.heima.wemedia.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.exception.CustomException;
import com.heima.file.service.FileStorageService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.thread.WmThreadLocalUtil;
import com.heima.utils.common.ImgUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;


@Service
@Slf4j
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB


    @Autowired
    private FileStorageService fileStorageService;


    @Override
    public WmMaterial uploadPicture(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        if (multipartFile.getSize() > MAX_IMAGE_SIZE){
            throw new CustomException(AppHttpCodeEnum.FILE_TOO_LARGE);
        }

        WmUser wmUser = WmThreadLocalUtil.getUser();
        if (wmUser == null){
            throw new CustomException(AppHttpCodeEnum.AP_USER_DATA_NOT_EXIST);
        }

        String url = "";

        try{
            byte[] bytes = multipartFile.getBytes();

            String suffix = "." + ImgUtil.detect(new ByteArrayInputStream(bytes));
            String uuid = UUID.randomUUID().toString().replace("-", "");
            String filename = uuid + suffix;
            url = fileStorageService.uploadImgFile(suffix, filename, new ByteArrayInputStream(bytes));

        } catch (IOException e){
            throw new CustomException(AppHttpCodeEnum.READ_FILE_FAILED);
        }

        if (StringUtils.isBlank(url)){
            throw new CustomException(AppHttpCodeEnum.SAVE_FILE_FAILED);
        }

        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUserId(wmUser.getId());
        wmMaterial.setUrl(url);
        wmMaterial.setType((short)0);
        wmMaterial.setIsCollection((short)0);
        wmMaterial.setCreatedTime(new Date());

        save(wmMaterial);

        return wmMaterial;
    }

    @Override
    public PageResponseResult findList(WmMaterialDto dto) {
        dto.checkParam();

        LambdaQueryWrapper<WmMaterial> wrapper = new LambdaQueryWrapper<>();

        if (dto.getIsCollection() == 1){
            wrapper.eq(WmMaterial::getIsCollection, dto.getIsCollection());
        }

        wrapper.eq(WmMaterial::getUserId, WmThreadLocalUtil.getUser().getId());

        wrapper.eq(WmMaterial::getIsDeleted, 0);

        wrapper.orderByDesc(WmMaterial::getCreatedTime);

        IPage<WmMaterial> page = new Page<>(dto.getPage(), dto.getSize());
        page = page(page, wrapper);

        PageResponseResult pageResponseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int)page.getTotal());
        pageResponseResult.setData(page.getRecords());
        return pageResponseResult;
    }

    @Override
    public ResponseResult addCollection(Integer id) {
        return updateCollectionStatus(id, 1);
    }

    @Override
    public ResponseResult cancelCollection(Integer id) {
        return updateCollectionStatus(id, 0);
    }

    private ResponseResult updateCollectionStatus(Integer id, int status){
        if (id == null || id <= 0){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        boolean update = lambdaUpdate()
                .eq(WmMaterial::getId, id)
                .set(WmMaterial::getIsCollection, status)
                .update();

        if (!update){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult delete(Integer id) {
        if (id == null || id <= 0){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        boolean update = lambdaUpdate()
                .eq(WmMaterial::getId, id)
                .set(WmMaterial::getIsDeleted, 1)
                .set(WmMaterial::getDeleteTime, new Date())
                .update();

        if (!update){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }


}
