//package com.heima.wemedia.service.impl;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.heima.apis.articlecore.ArticleCoreClient;
//import com.heima.model.articlecore.entity.ArticleChannel;
//import com.heima.model.articlecore.vo.ChannelVo;
//import com.heima.model.common.enums.AppHttpCodeEnum;
//import com.heima.model.wemedia.dtos.WmChannelAddDto;
//import com.heima.model.wemedia.dtos.WmChannelPageReqDto;
//import com.heima.model.common.dtos.PageResponseResult;
//import com.heima.model.common.dtos.ResponseResult;
//import com.heima.model.wemedia.dtos.WmChannelUpdateDto;
//import com.heima.model.wemedia.pojos.WmChannel;
//import com.heima.wemedia.mapper.WmChannelMapper;
//import com.heima.wemedia.service.WmChannelService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//@Service
//@Slf4j
//public class WmChannelServiceImpl extends ServiceImpl<WmChannelMapper, WmChannel> implements WmChannelService {
//
//    @Autowired
//    private WmChannelMapper wmChannelMapper;
//
//    @Autowired
//    private ArticleCoreClient articleCoreClient;
//
//    @Override
//    public List<WmChannel> findAll() {
//
//        List<WmChannel> list = lambdaQuery()
//                .eq(WmChannel::getStatus, 1)
//                .orderByAsc(WmChannel::getOrd)
//                .list();
//
//        return list;
//    }
//
//    @Override
//    public List<ChannelVo> getChannelList() {
//        List<ArticleChannel> channelList = articleCoreClient.getChannelList();
//        List<ChannelVo> channelVoList = new ArrayList<>();
//        for (ArticleChannel channel : channelList) {
//            ChannelVo channelVo = new ChannelVo();
//            BeanUtils.copyProperties(channel, channelVo);
//            channelVo.setOrd(channel.getSort());
//            channelVoList.add(channelVo);
//        }
//
//        return channelVoList;
//    }
//
//    @Override
//    public ResponseResult pageList(WmChannelPageReqDto dto) {
//
//        if (dto.getPage() <= 1){
//            dto.setPage(1);
//        }
//
//        if (dto.getSize() <= 10){
//            dto.setSize(10);
//        }
//
//        LambdaQueryWrapper<WmChannel> wrapper = new LambdaQueryWrapper<>();
//
//        if (StringUtils.isNotBlank(dto.getName())){
//            wrapper.like(WmChannel::getName, dto.getName());
//        }
//
//        wrapper.orderByAsc(WmChannel::getOrd)
//                .orderByDesc(WmChannel::getCreatedTime);
//
//        IPage<WmChannel> iPage = new Page<>(dto.getPage(), dto.getSize());
//
//        IPage<WmChannel> pageResult = page(iPage, wrapper);
//
//        PageResponseResult pageResponseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int)pageResult.getTotal());
//        pageResponseResult.setData(pageResult.getRecords());
//
//        return pageResponseResult;
//    }
//
//    @Override
//    public ResponseResult save(WmChannelAddDto dto) {
//        if (StringUtils.isBlank(dto.getName())){
//            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
//        }
//
//        WmChannel wmChannel = new WmChannel();
//        BeanUtils.copyProperties(dto, wmChannel);
//
//        wmChannel.setIsDefault(true);
//        wmChannel.setCreatedTime(new Date());
//
//        wmChannelMapper.insert(wmChannel);
//
//        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
//    }
//
//    @Override
//    public ResponseResult update(WmChannelUpdateDto dto) {
//        if (dto.getId() == null){
//            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
//        }
//
//        WmChannel channel = getById(dto.getId());
//
//        if (channel == null){
//            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
//        }
//
//        if (StringUtils.isNotBlank(dto.getName())){
//            channel.setName(dto.getName());
//        }
//
//        if (dto.getDescription() != null){
//            channel.setDescription(dto.getDescription());
//        }
//
//        if (dto.getIsDefault() != null){
//            channel.setIsDefault(dto.getIsDefault());
//        }
//
//        if (dto.getStatus() != null){
//            channel.setStatus(dto.getStatus());
//        }
//
//        if (dto.getOrd() != null){
//            channel.setOrd(dto.getOrd());
//        }
//
//        updateById(channel);
//
//        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
//    }
//
//    @Override
//    public ResponseResult delete(Integer id) {
//        if (id == null){
//            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
//        }
//
//        removeById(id);
//
//        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
//    }
//
//
//}
