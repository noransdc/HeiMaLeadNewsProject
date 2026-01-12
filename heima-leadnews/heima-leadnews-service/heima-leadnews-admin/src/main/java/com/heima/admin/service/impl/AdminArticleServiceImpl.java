package com.heima.admin.service.impl;

import com.heima.admin.service.AdminArticleService;
import com.heima.apis.articlecore.ArticleCoreClient;
import com.heima.apis.wemedia.WeMediaClient;
import com.heima.model.articlecore.dto.AdminArticlePageDto;
import com.heima.model.articlecore.dto.ArticleAuthFailDto;
import com.heima.model.articlecore.vo.AdminArticleListVo;
import com.heima.model.articlecore.vo.AuthorArticleDetailVo;
import com.heima.model.common.dtos.PageResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class AdminArticleServiceImpl implements AdminArticleService {

    @Autowired
    private ArticleCoreClient articleCoreClient;

    @Autowired
    private WeMediaClient weMediaClient;

    @Override
    public PageResponseResult<List<AdminArticleListVo>> pageForAdmin(AdminArticlePageDto dto) {
        dto.checkParam();

        PageResponseResult<List<AdminArticleListVo>> pageRsp = articleCoreClient.pageForAdmin(dto);

        List<Long> ids = pageRsp.getData().stream()
                .map(AdminArticleListVo::getAuthorId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> nameMap = weMediaClient.getAuthorNameMap(ids);

        pageRsp.getData().forEach(item->{
            item.setAuthorName(nameMap.get(item.getAuthorId()));
        });

        return pageRsp;
    }

    @Override
    public AuthorArticleDetailVo detailForAdmin(Long id) {
        AuthorArticleDetailVo detailVo = articleCoreClient.detailForAdmin(id);
        List<Long> ids = new ArrayList<>();
        ids.add(detailVo.getAuthorId());
        Map<Long, String> nameMap = weMediaClient.getAuthorNameMap(ids);
        detailVo.setAuthorName(nameMap.get(detailVo.getAuthorId()));
        return detailVo;
    }

    @Override
    public void manualAuditReject(ArticleAuthFailDto dto) {
        articleCoreClient.manualAuditReject(dto);
    }

    @Override
    public void manualAuditPass(Long articleId) {
        articleCoreClient.manualAuditPass(articleId);
    }


}
