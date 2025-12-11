package com.heima.search.service.impl;

import com.heima.common.constants.ElasticSearchConstant;
import com.heima.model.search.dto.UserSearchDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.search.pojo.ApUserSearch;
import com.heima.search.service.ArticleSearchService;
import com.heima.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class ArticleSearchServiceImpl implements ArticleSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public ResponseResult search(UserSearchDto dto) throws IOException {
        if (dto == null || StringUtils.isBlank(dto.getSearchWords())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        if (dto.getPageNum() < 1){
            dto.setPageNum(0);
        }

        if (dto.getPageSize() < 10){
            dto.setPageSize(10);
        }

        if (dto.getMinBehotTime() == null){
            dto.setMinBehotTime(new Date());
        }

        ApUser user = AppThreadLocalUtil.getUser();
        if (user != null && dto.getPageNum() == 0){
            insert(dto.getSearchWords(), user.getId());
        }


        Date currentDate = new Date();
        log.info("currentDate:{}", currentDate);

        //is similar to database name and table name
        String EsIndexName = ElasticSearchConstant.APP_INFO_ARTICLE;

        SearchRequest searchRequest = new SearchRequest(EsIndexName);

        //关键字的分词之后查询
        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(dto.getSearchWords())
                .field("title")//is similar to column name
                .field("content")
                .defaultOperator(Operator.OR);

        //查询小于mindate的数据
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("publishTime")
                .lt(dto.getMinBehotTime());

        //布尔查询
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(queryStringQueryBuilder);
        boolQueryBuilder.filter(rangeQueryBuilder);


        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //分页查询
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(dto.getPageSize());
        //按照发布时间倒序查询
        searchSourceBuilder.sort("publishTime", SortOrder.DESC);

        //设置高亮  title
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<font style='color: red; font-size: inherit;'>");
        highlightBuilder.postTags("</font>");
        searchSourceBuilder.highlighter(highlightBuilder);

        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        List<Map<String, Object>> mapList = new ArrayList<>();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            if (hit.getHighlightFields() != null && hit.getHighlightFields().size() > 0){
                Text[] titles = hit.getHighlightFields().get("title").getFragments();
                map.put("h_title", StringUtils.join(titles));
            } else {
                map.put("h_title", map.get("title"));
            }

            mapList.add(map);
        }

        return ResponseResult.okResult(mapList);
    }

    @Override
    @Async
    public void insert(String keywords, Integer userId) {

        if (StringUtils.isBlank(keywords)){
            return;
        }

        Criteria condition = Criteria.where("keyword").is(keywords).and("userId").is(userId);
        ApUserSearch cache = mongoTemplate.findOne(Query.query(condition), ApUserSearch.class);

        if (cache != null){
            cache.setCreatedTime(new Date());
            mongoTemplate.save(cache);
            return;
        }

        ApUserSearch apUserSearch = new ApUserSearch();
        apUserSearch.setUserId(userId);
        apUserSearch.setKeyword(keywords);
        apUserSearch.setCreatedTime(new Date());

        Criteria listCondition = Criteria.where("userId").is(userId);
        Query listQuery = Query.query(listCondition);
        listQuery.with(Sort.by(Sort.Direction.DESC, "createTime"));
        List<ApUserSearch> searchList = mongoTemplate.find(listQuery, ApUserSearch.class);

        if (searchList.size() < 10 ){
            mongoTemplate.save(apUserSearch);

        } else {
            ApUserSearch lastItem = searchList.get(searchList.size() - 1);
            Query lastQuery = Query.query(Criteria.where("id").is(lastItem.getId()));
            mongoTemplate.findAndReplace(lastQuery, apUserSearch);
        }

    }


}
