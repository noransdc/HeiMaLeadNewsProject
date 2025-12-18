package com.heima.wemedia.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmSensitivePageDto;
import com.heima.model.wemedia.dtos.WmSensitiveAddDto;
import com.heima.model.wemedia.dtos.WmSensitiveUpdateDto;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.service.WmSensitiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class WmSensitiveServiceImpl extends ServiceImpl<WmSensitiveMapper, WmSensitive> implements WmSensitiveService {

    @Autowired
    private WmSensitiveMapper wmSensitiveMapper;

    @Override
    public void add(WmSensitiveAddDto dto) {
        WmSensitive wmSensitive = new WmSensitive();
        wmSensitive.setSensitives(dto.getSensitives());
        wmSensitive.setCreatedTime(new Date());
        wmSensitiveMapper.insert(wmSensitive);
    }

    @Override
    public void delete(Integer id) {
        removeById(id);
    }

    @Override
    public void update(WmSensitiveUpdateDto dto) {
        WmSensitive wmSensitive = getById(dto.getId());
        if (wmSensitive == null){
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        wmSensitive.setSensitives(dto.getSensitives());

        updateById(wmSensitive);
    }

    @Override
    public IPage<WmSensitive> pageList(WmSensitivePageDto dto) {
        if (dto.getPage() < 1){
            dto.setPage(1);
        }
        if (dto.getSize() < 10){
            dto.setSize(10);
        }

        LambdaQueryWrapper<WmSensitive> query = Wrappers.lambdaQuery();

        if (StringUtils.isNotBlank(dto.getName())){
            query.like(WmSensitive::getSensitives, dto.getName());
        }

        query.orderByDesc(WmSensitive::getCreatedTime);

        IPage<WmSensitive> iPage = new Page<>(dto.getPage(), dto.getSize());
        IPage<WmSensitive> result = page(iPage, query);

        return result;
    }


}
