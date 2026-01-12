package com.heima.wemedia.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.apis.articlecore.ArticleCoreClient;
import com.heima.common.exception.CustomException;
import com.heima.model.articlecore.dto.ArticleSubmitDto;
import com.heima.model.articlecore.dto.AuthorArticlePageDto;
import com.heima.model.articlecore.vo.AuthorArticleDetailVo;
import com.heima.model.articlecore.vo.AuthorArticleListVo;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {


    @Autowired
    private ArticleCoreClient articleCoreClient;


    @Override
    public AuthorArticleDetailVo getArticleVo(Long articleId) {
        if (articleId == null){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID, "文章id不能为null");
        }
        return articleCoreClient.detailForAuthor(articleId);
    }


    @Override
    public PageResponseResult<List<AuthorArticleListVo>> getPageListRemote(WmNewsPageReqDto dto) {
        WmUser wmUser = WmThreadLocalUtil.getUser();
        if (wmUser == null){
            throw new CustomException(AppHttpCodeEnum.USER_NOT_EXIST);
        }
        AuthorArticlePageDto authorArticlePageDto = new AuthorArticlePageDto();
        BeanUtils.copyProperties(dto, authorArticlePageDto);
        authorArticlePageDto.setAuthorId(wmUser.getId());
        return articleCoreClient.pageForAuthor(authorArticlePageDto);
    }

//    @Override
//    public ResponseResult submitNews(WmNewsDto dto) {
//        if (dto == null || dto.getType() == null) {
//            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
//        }
//
//        String content = dto.getContent();
//        if (StringUtils.isEmpty(content)) {
//            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
//        }
//
//        WmNews wmNews = new WmNews();
//        BeanUtils.copyProperties(dto, wmNews);
//
//        List<String> contentUrlList = parseImgUrlList(content);
//        List<String> coverUrlList = getCoverUrlList(dto, contentUrlList);
//
//        if (!CollectionUtils.isEmpty(coverUrlList)) {
//            wmNews.setImages(StringUtils.join(coverUrlList, ","));
//            if (coverUrlList.size() == 3) {
//                wmNews.setType(WeMediaConstants.WM_NEWS_MANY_IMAGE);
//            } else if (coverUrlList.size() == 1) {
//                wmNews.setType(WeMediaConstants.WM_NEWS_SINGLE_IMAGE);
//            } else {
//                wmNews.setType(WeMediaConstants.WM_NEWS_NONE_IMAGE);
//            }
//        } else {
//            wmNews.setType(WeMediaConstants.WM_NEWS_NONE_IMAGE);
//        }
//
//        List<WmMaterial> contentMaterialList = getMaterialList(contentUrlList);
//        List<WmMaterial> coverMaterialList = getMaterialList(coverUrlList);
//
//        wmNews.setUserId(WmThreadLocalUtil.getUser().getId());
//        Date now = new Date();
//        wmNews.setCreatedTime(now);
//        wmNews.setSubmitedTime(now);
//        wmNews.setEnable((short) 1);
//
//        if (wmNews.getId() == null) {
//            wmNews.setReason("审核中");
//            save(wmNews);
//        } else {
//            LambdaQueryWrapper<WmNewsMaterial> wrapper = new LambdaQueryWrapper<>();
//            wrapper.eq(WmNewsMaterial::getNewsId, wmNews.getId());
//            wmNewsMaterialMapper.delete(wrapper);
//            updateById(wmNews);
//        }
//
//        if (!contentMaterialList.isEmpty()) {
//            saveRelation(contentMaterialList, wmNews, WeMediaConstants.WM_CONTENT_REFERENCE);
//        }
//
//        if (!coverMaterialList.isEmpty()) {
//            saveRelation(coverMaterialList, wmNews, WeMediaConstants.WM_COVER_REFERENCE);
//        }
//
////        wmAutoScanService.autoScanWmNews(wmNews.getId());
//        wmScheduleService.addNewsToTask(wmNews.getId(), dto.getPublishTime());
//
//        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
//    }


//    private void saveRelation(List<WmMaterial> materialList, WmNews wmNews, Short type) {
//        List<Integer> idList = materialList.stream().map(WmMaterial::getId).collect(Collectors.toList());
//        wmNewsMaterialMapper.saveRelations(idList, wmNews.getId(), type);
//
//    }


    @Override
    public void submitRemote(WmNewsDto dto) {
        if (dto == null){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        WmUser wmUser = WmThreadLocalUtil.getUser();
        if (wmUser == null){
            throw new CustomException(AppHttpCodeEnum.USER_NOT_EXIST);
        }

        String title = dto.getTitle();
        String content = dto.getContent();
        Integer channelId = dto.getChannelId();
        Short draftStatus = dto.getStatus();
        Short coverType = dto.getType();

        if (StringUtils.isBlank(title) || StringUtils.isBlank(content) || channelId == null || draftStatus == null || coverType == null){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        ArticleSubmitDto articleSubmitDto = new ArticleSubmitDto();
        articleSubmitDto.setTitle(dto.getTitle());
        articleSubmitDto.setContent(dto.getContent());
        articleSubmitDto.setChannelId((long)channelId);
        articleSubmitDto.setAuthorId((long) wmUser.getId());
        articleSubmitDto.setPublishTime(dto.getPublishTime());
        articleSubmitDto.setImages(dto.getImages());
        articleSubmitDto.setLabel(dto.getLabels());
        articleSubmitDto.setCoverType((int)coverType);

        if (draftStatus == 0){
            articleSubmitDto.setIsDraft(1);
        } else {
            articleSubmitDto.setIsDraft(0);
        }

        articleCoreClient.submit(articleSubmitDto);
    }


}
