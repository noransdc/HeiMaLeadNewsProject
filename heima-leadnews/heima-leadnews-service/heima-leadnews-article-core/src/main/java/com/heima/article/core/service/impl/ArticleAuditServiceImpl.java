package com.heima.article.core.service.impl;


import com.alibaba.fastjson.JSON;
import com.heima.article.core.mapper.ArticleSensitiveMapper;
import com.heima.article.core.service.ArticleAuditService;
import com.heima.article.core.service.ArticleSensitiveService;
import com.heima.article.core.service.ArticleService;
import com.heima.common.aliyun.GreenImageScanV2;
import com.heima.common.aliyun.GreenTextScanV1;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.enums.ArticleAuditEnum;
import com.heima.common.enums.GreenScanEnum;
import com.heima.common.exception.CustomException;
import com.heima.file.service.FileStorageService;
import com.heima.model.articlecore.dto.ArticleDetailDto;
import com.heima.model.articlecore.dto.GreenScanRspDto;
import com.heima.model.articlecore.entity.Article;
import com.heima.model.articlecore.entity.ArticleContent;
import com.heima.model.articlecore.entity.ArticleContentItem;
import com.heima.model.common.enums.AppHttpCodeEnum;
import feign.template.Literal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.*;


@Service
@Slf4j
public class ArticleAuditServiceImpl implements ArticleAuditService {


    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleSensitiveService articleSensitiveService;

    @Autowired
    private GreenTextScanV1 greenTextScanV1;

    @Autowired
    private GreenImageScanV2 greenImageScanV2;

    @Autowired
    private FileStorageService fileStorageService;



    @Async
    @Override
    public void audit(Long articleId) {

        ArticleDetailDto articleDetail = articleService.getArticleDetail(articleId);

        Map<String, List<String>> map = getMaterial(articleDetail);

        List<String> textList = map.get(ArticleConstants.CONTENT_ITEM_TEXT);
        List<String> urlList = map.get(ArticleConstants.CONTENT_ITEM_IMG);

        String text = StringUtils.join(textList, ",");
        if (!auditTextLocal(articleId, text)){
            return;
        }

        if (!auditTextThirdParty(articleId, text)){
            return;
        }

        if (!auditImages(articleId, urlList)){
            return;
        }

        articleService.updateAuditStatus(articleId, ArticleAuditEnum.AUDIT_SUCCESS, null);

    }

    private Map<String, List<String>> getMaterial(ArticleDetailDto articleDetail){
        Article article = articleDetail.getArticle();
        String title = article.getTitle();
        String content = articleDetail.getArticleContent().getContent();

        List<String> textList = new ArrayList<>();
        textList.add(title);

        List<String> urlList = new ArrayList<>();

        String coverImgUrl = article.getCoverImgUrl();
        if (StringUtils.isNotBlank(coverImgUrl)){
            String[] split = coverImgUrl.split(",");
            if (split.length > 0){
                urlList.addAll(Arrays.asList(split));
            }
        }

        List<ArticleContentItem> contentItemList = JSON.parseArray(content, ArticleContentItem.class);
        for (ArticleContentItem item : contentItemList) {
            if (ArticleConstants.CONTENT_ITEM_TEXT.equals(item.getType())){
                textList.add(item.getValue());
            } else if (ArticleConstants.CONTENT_ITEM_IMG.equals(item.getType())){
                urlList.add(item.getValue());
            }
        }

        Map<String, List<String>> map = new HashMap<>();
        map.put(ArticleConstants.CONTENT_ITEM_TEXT, textList);
        map.put(ArticleConstants.CONTENT_ITEM_IMG, urlList);

        return map;
    }

    private boolean auditTextLocal(Long articleId, String text){
        Map<String, Integer> scan = articleSensitiveService.scan(text);
        if (scan.isEmpty()){
            return true;
        }
        articleService.updateAuditStatus(articleId, ArticleAuditEnum.AUTO_AUDIT_FAILED, scan.toString());
        return false;
    }

    private boolean auditTextThirdParty(Long articleId, String text){
        try {
            GreenScanRspDto rsp = greenTextScanV1.scan(text);
            if (rsp.getRiskLevel() == GreenScanEnum.PASS.getCode()){
                return true;
            } else {
                articleService.updateAuditStatus(articleId, ArticleAuditEnum.AUTO_AUDIT_FAILED, rsp.getSuggestion());
                return false;
            }

        } catch (Exception e){
            log.warn("auditTextThirdParty error:{}", e.getMessage());
            articleService.updateAuditStatus(articleId, ArticleAuditEnum.AUTO_AUDIT_FAILED, e.getMessage());
        }

        return false;
    }

    private boolean auditImages(Long articleId, List<String> urlList){
        if (urlList.isEmpty()){
            return true;
        }
        for (String url : urlList) {
            byte[] bytes = fileStorageService.downLoadFile(url);
//            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
//            BufferedImage read = ImageIO.read(inputStream);
        }

        try {
            GreenScanRspDto rsp = greenImageScanV2.scan(urlList);
            if (rsp.getRiskLevel() == GreenScanEnum.PASS.getCode()){
                return true;
            } else {
                articleService.updateAuditStatus(articleId, ArticleAuditEnum.AUTO_AUDIT_FAILED, rsp.getSuggestion());
                return false;
            }

        } catch (Exception e){
            log.info("audit images error:{}", e.getMessage());
            articleService.updateAuditStatus(articleId, ArticleAuditEnum.AUTO_AUDIT_FAILED, e.getMessage());
        }

        return false;
    }



}
