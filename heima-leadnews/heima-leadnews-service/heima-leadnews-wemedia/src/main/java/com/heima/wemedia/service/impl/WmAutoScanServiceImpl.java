//package com.heima.wemedia.service.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.heima.apis.article.IArticleClient;
//import com.heima.common.aliyun.GreenImageScanV2;
//import com.heima.common.aliyun.GreenTextScanV1;
//import com.heima.common.tess4j.Tess4jClient;
//import com.heima.file.service.FileStorageService;
//import com.heima.model.article.dtos.ArticleDto;
//import com.heima.model.common.dtos.ResponseResult;
//import com.heima.model.common.enums.AppHttpCodeEnum;
//import com.heima.model.wemedia.pojos.WmChannel;
//import com.heima.model.wemedia.pojos.WmNews;
//import com.heima.model.wemedia.pojos.WmSensitive;
//import com.heima.model.wemedia.pojos.WmUser;
//import com.heima.utils.common.SensitiveWordUtil;
//import com.heima.wemedia.mapper.WmChannelMapper;
//import com.heima.wemedia.mapper.WmNewsMapper;
//import com.heima.wemedia.mapper.WmSensitiveMapper;
//import com.heima.wemedia.mapper.WmUserMapper;
//import com.heima.wemedia.service.WmAutoScanService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.util.*;
//import java.util.stream.Collectors;
//
//
//@Service
//@Slf4j
//@Transactional
//public class WmAutoScanServiceImpl implements WmAutoScanService {
//
//    @Autowired
//    private WmNewsMapper wmNewsMapper;
//
//    @Autowired
//    private GreenTextScanV1 greenTextScanV1;
//
//    @Autowired
//    private GreenImageScanV2 greenImageScanV2;
//
//    @Autowired
//    private IArticleClient articleClient;
//
//    @Autowired
//    private WmChannelMapper wmChannelMapper;
//
//    @Autowired
//    private WmUserMapper wmUserMapper;
//
//    @Autowired
//    private WmSensitiveMapper wmSensitiveMapper;
//
//    @Autowired
//    private FileStorageService fileStorageService;
//
//    @Autowired
//    private Tess4jClient tess4jClient;
//
//
//    @Override
//    @Async
//    public void autoScanWmNews(Integer id) {
//        WmNews wmNews = wmNewsMapper.selectById(id);
//        if (wmNews == null){
//            throw new RuntimeException("WmAutoScanServiceImpl-文章不存在");
//        }
//
//        if (wmNews.getStatus() != WmNews.Status.SUBMIT.getCode()){
//            return;
//        }
//
//        Map<String, List<String>> parseMap = parseContent(wmNews);
//        List<String> textList = parseMap.get("text");
//        List<String> images = parseMap.get("images");
//
//        boolean localScanSensitive = localScanSensitive(textList, wmNews);
//        log.info("localScanSensitive:{}", localScanSensitive);
//        if (!localScanSensitive){
//            return;
//        }
//
//        boolean scanTextResult = scanText(textList, wmNews);
//        log.info("scanTextResult:{}", scanTextResult);
//        if (!scanTextResult){
//            return;
//        }
//
//        boolean scanImagesResult = scanImages(images, wmNews);
//        log.info("scanImagesResult:{}", scanImagesResult);
//        if (!scanImagesResult){
//            return;
//        }
//
//        ResponseResult responseResult = saveArticle(wmNews);
//
//        log.info("autoScanWmNews responseResult:{}", responseResult.getCode());
//        if (AppHttpCodeEnum.SUCCESS.getCode() != responseResult.getCode()){
//            throw new RuntimeException("WmNewsAutoScanServiceImpl-文章审核，保存app端相关文章数据失败");
//        }
//
//        wmNews.setStatus(WmNews.Status.PUBLISHED.getCode());
//        wmNews.setReason("发布成功");
//
//        Object data = responseResult.getData();
//        if (data instanceof Long){
//            wmNews.setArticleId((Long) data);
//        }
//
//        wmNewsMapper.updateById(wmNews);
//    }
//
//    private boolean localScanSensitive(List<String> textList, WmNews wmNews){
//        if (textList.isEmpty()){
//            return true;
//        }
//        String text = textList.get(0);
//        if (StringUtils.isBlank(text)){
//            return true;
//        }
//        LambdaQueryWrapper<WmSensitive> wrapper = new LambdaQueryWrapper<>();
//        wrapper.select(WmSensitive::getSensitives);
//        List<WmSensitive> sensitives = wmSensitiveMapper.selectList(wrapper);
//        if (sensitives.isEmpty()){
//            return true;
//        }
//        List<String> sensitiveWords = sensitives.stream().map(WmSensitive::getSensitives).collect(Collectors.toList());
//        if (sensitiveWords.isEmpty()){
//            return true;
//        }
//        SensitiveWordUtil.initMap(sensitiveWords);
//
//        Map<String, Integer> map = SensitiveWordUtil.matchWords(text);
//        if (map.isEmpty()){
//            return true;
//        }
//        wmNews.setStatus(WmNews.Status.FAIL.getCode());
//        wmNews.setReason(map.toString());
//        wmNewsMapper.updateById(wmNews);
//
//        return false;
//    }
//
//    private ResponseResult saveArticle(WmNews wmNews){
//
//        ArticleDto dto = new ArticleDto();
//        BeanUtils.copyProperties(wmNews, dto);
//
//        dto.setAuthorId((long)wmNews.getUserId());
//
//        WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
//        if (wmUser != null){
//            dto.setAuthorName(wmUser.getName());
//        }
//
//        WmChannel wmChannel = wmChannelMapper.selectById(wmNews.getChannelId());
//        if (wmChannel != null){
//            dto.setChannelName(wmChannel.getName());
//        }
//
//        dto.setLayout(wmNews.getType());
//        dto.setId(wmNews.getArticleId());
//
//
//        ResponseResult result = articleClient.saveArticle(dto);
//        return result;
//    }
//
//    private boolean scanText(List<String> list, WmNews wmNews){
//        if (list.isEmpty()){
//            return true;
//        }
//
//        String text = list.get(0);
//        if (StringUtils.isBlank(text)){
//            return true;
//        }
//
//        try {
//
////            Map<String, String> map = greenTextScanV1.scan(text);
////            return processScanResult(map, wmNews);
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//
//        return false;
//    }
//
//    private boolean scanImages(List<String> list, WmNews wmNews){
//        if (list.isEmpty()){
//            return true;
//        }
//        try {
//            for (String s : list) {
//                byte[] bytes = fileStorageService.downLoadFile(s);
//                ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
//                BufferedImage read = ImageIO.read(inputStream);
//                if (read != null){
//                    String result = tess4jClient.doOCR(read);
//                    if (StringUtils.isNotBlank(result)){
//                        List<String> textList = new ArrayList<>();
//                        textList.add(result);
//                        boolean scanSensitiveResult = localScanSensitive(textList, wmNews);
//                        if (!scanSensitiveResult){
//                            return false;
//                        }
//                    }
//                }
//            }
//
////            Map<String, String> map = greenImageScanV2.scan(list);
////            return processScanResult(map, wmNews);
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//
//        return false;
//    }
//
//    private boolean processScanResult(Map<String, String> map, WmNews wmNews){
//        boolean flag = false;
//        String suggestion = map.get("suggestion");
//        String label = map.get("label");
//        if ("none".equals(label)){
//            wmNews.setStatus(WmNews.Status.SUCCESS.getCode());
//            wmNews.setReason("审核成功");
//            flag = true;
//
//        } else if ("high".equals(label)){
//            wmNews.setStatus(WmNews.Status.FAIL.getCode());
//            wmNews.setReason(suggestion);
//
//        } else {
//            wmNews.setStatus(WmNews.Status.ADMIN_AUTH.getCode());
//            wmNews.setReason(suggestion);
//        }
//
//        wmNewsMapper.updateById(wmNews);
//
//        return flag;
//    }
//
//    private Map<String, List<String>> parseContent(WmNews wmNews){
//        String content = wmNews.getContent();
//        List<Map> maps = JSON.parseArray(content, Map.class);
//        List<String> urlList = new ArrayList<>();
//
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(wmNews.getTitle());
//
//        for (Map map : maps) {
//            if ("text".equals(map.get("type"))){
//                stringBuilder.append(map.get("value"));
//            } else if ("image".equals(map.get("type"))){
//                Object value = map.get("value");
//                if (value instanceof String){
//                    urlList.add((String) value);
//                }
//            }
//        }
//
//        String images = wmNews.getImages();
//        if (StringUtils.isNotBlank(images)){
//            String[] split = images.split(",");
//            urlList.addAll(Arrays.asList(split));
//        }
//
//        List<String> collect = urlList.stream().distinct().collect(Collectors.toList());
//
//        Map<String, List<String>> resultMap = new HashMap<>();
//        List<String> textList = new ArrayList<>();
//        textList.add(stringBuilder.toString());
//        resultMap.put("text", textList);
//        resultMap.put("images", collect);
//
//        return resultMap;
//    }
//
//
//
//}
