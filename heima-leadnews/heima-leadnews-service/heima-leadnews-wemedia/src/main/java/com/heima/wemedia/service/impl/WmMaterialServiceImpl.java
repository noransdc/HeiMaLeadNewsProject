package com.heima.wemedia.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.file.service.FileStorageService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.UUID;


@Service
@Slf4j
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {


    @Autowired
    private FileStorageService fileStorageService;


    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.getSize() == 0){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        String originalFilename = multipartFile.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)){
            originalFilename = "file.jpg";
        }
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String filename = uuid + suffix;

        String url = null;
        try {
            url = fileStorageService.uploadImgFile("", filename, multipartFile.getInputStream());
            log.info("上传图片到minIO中，url:{}", url);

        } catch (Exception e){
            log.error("WmMaterialServiceImpl 上传图片失败");
            e.printStackTrace();
        }

        WmUser wmUser = WmThreadLocalUtil.getUser();
        Integer userId = null;
        if (wmUser != null){
            userId = wmUser.getId();
        }

        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUserId(userId);
        wmMaterial.setUrl(url);
        wmMaterial.setType((short)0);
        wmMaterial.setIsCollection((short)0);
        wmMaterial.setCreatedTime(new Date());

        save(wmMaterial);

        return ResponseResult.okResult(wmMaterial);
    }

    @Override
    public PageResponseResult findList(WmMaterialDto dto) {
        dto.checkParam();

        LambdaQueryWrapper<WmMaterial> wrapper = new LambdaQueryWrapper<>();

        if (dto.getIsCollection() == 1){
            wrapper.eq(WmMaterial::getIsCollection, dto.getIsCollection());
        }

        wrapper.eq(WmMaterial::getUserId, WmThreadLocalUtil.getUser().getId());

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


}
