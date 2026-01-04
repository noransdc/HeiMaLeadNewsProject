package com.heima.common.aliyun;


import com.alibaba.fastjson.JSON;
import com.aliyun.green20220302.Client;
import com.aliyun.green20220302.models.ImageModerationRequest;
import com.aliyun.green20220302.models.ImageModerationResponse;
import com.aliyun.green20220302.models.ImageModerationResponseBody;
import com.aliyun.green20220302.models.ImageModerationResponseBody.ImageModerationResponseBodyData;
import com.aliyun.green20220302.models.ImageModerationResponseBody.ImageModerationResponseBodyDataResult;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.heima.common.enums.GreenScanEnum;
import com.heima.model.articlecore.dto.GreenScanRspDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aliyun")
public class GreenImageScanV2 {

    private String accessKeyId;
    private String secret;


    public GreenScanRspDto scan(List<String> urlList) throws Exception {
        Map<String, String> resultMap = new HashMap<>();

//        int i = new Random().nextInt(3);
//        System.out.println("i:" + i);
//        if (i == 0){
//            resultMap.put("suggestion", "成人色情");
//            resultMap.put("label", "high");
//        } else {
//            resultMap.put("suggestion", "pass");
//            resultMap.put("label", "none");
//
//        }

        resultMap.put("suggestion", "pass");
        resultMap.put("label", "none");



        return new GreenScanRspDto(GreenScanEnum.PASS.getCode(), "pass");
    }







}
