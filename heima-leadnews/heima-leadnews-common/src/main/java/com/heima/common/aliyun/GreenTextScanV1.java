package com.heima.common.aliyun;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.green20220302.Client;
import com.aliyun.green20220302.models.TextModerationPlusRequest;
import com.aliyun.green20220302.models.TextModerationPlusResponse;
import com.aliyun.green20220302.models.TextModerationPlusResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.heima.common.enums.GreenScanEnum;
import com.heima.model.articlecore.dto.GreenScanRspDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aliyun")
public class GreenTextScanV1 {


    private String accessKeyId;
    private String secret;

    public GreenScanRspDto scan(String content) throws Exception {
        Config config = new Config();
        /**
         * 阿里云账号AccessKey拥有所有API的访问权限，建议您使用RAM用户进行API访问或日常运维。
         * 常见获取环境变量方式：
         * 方式一：
         *     获取RAM用户AccessKey ID：System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID");
         *     获取RAM用户AccessKey Secret：System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET");
         * 方式二：
         *     获取RAM用户AccessKey ID：System.getProperty("ALIBABA_CLOUD_ACCESS_KEY_ID");
         *     获取RAM用户AccessKey Secret：System.getProperty("ALIBABA_CLOUD_ACCESS_KEY_SECRET");
         */
        config.setAccessKeyId(accessKeyId);
        config.setAccessKeySecret(secret);
        //接入区域和地址请根据实际情况修改
        config.setRegionId("cn-shanghai");
        config.setEndpoint("green-cip.cn-shanghai.aliyuncs.com");
        //读取时超时时间，单位毫秒（ms）。
        config.setReadTimeout(6000);
        //连接时超时时间，单位毫秒（ms）。
        config.setConnectTimeout(3000);
        //设置http代理。
        //config.setHttpProxy("http://xx.xx.xx.xx:xxxx");
        //设置https代理。
        //config.setHttpsProxy("https://xx.xx.xx.xx:xxxx");
        Client client = new Client(config);

        JSONObject serviceParameters = new JSONObject();
        serviceParameters.put("content", content);

        TextModerationPlusRequest textModerationPlusRequest = new TextModerationPlusRequest();
        // 检测类型
        textModerationPlusRequest.setService("comment_detection_pro");
        textModerationPlusRequest.setServiceParameters(serviceParameters.toJSONString());

        try {
            TextModerationPlusResponse response = client.textModerationPlus(textModerationPlusRequest);
            if (response.getStatusCode() != 200){
                String msg = "response not success. status:" + response.getStatusCode();
                return getErrorRsp(msg);
            }

            TextModerationPlusResponseBody result = response.getBody();
            Integer code = result.getCode();

            if (code != 200){
                String msg = "text moderation not success. code:" + code;
                return getErrorRsp(msg);
            }

//            System.out.println(JSON.toJSONString(result));
//            System.out.println("requestId = " + result.getRequestId());
//            System.out.println("code = " + result.getCode());
//            System.out.println("msg = " + result.getMessage());

            TextModerationPlusResponseBody.TextModerationPlusResponseBodyData data = result.getData();
//            System.out.println(JSON.toJSONString(data, true));

            if (data == null){
                return getErrorRsp("data is null");
            }

            //none, high
            String riskLevel = data.riskLevel;
            String description = "";
            for (TextModerationPlusResponseBody.TextModerationPlusResponseBodyDataResult dataResult : data.result) {
                description = dataResult.description;
            }

            GreenScanRspDto greenScanRspDto = new GreenScanRspDto();
            greenScanRspDto.setSuggestion(description);
            switch (riskLevel){
                case "none":
                    greenScanRspDto.setRiskLevel(GreenScanEnum.PASS.getCode());
                    break;

                case "high":
                    greenScanRspDto.setRiskLevel(GreenScanEnum.HIGH_RISK.getCode());
                    break;

                default:
                    greenScanRspDto.setRiskLevel(GreenScanEnum.PENDING_REVIEW.getCode());
                    break;
            }


            return greenScanRspDto;

        } catch (Exception e) {
            return getErrorRsp(e.getMessage());
        }

    }

    private GreenScanRspDto getErrorRsp(String msg){
        return new GreenScanRspDto(GreenScanEnum.SERVER_ERROR.getCode(), msg);
    }


}
