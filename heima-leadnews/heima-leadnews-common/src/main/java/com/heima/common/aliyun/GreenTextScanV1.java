package com.heima.common.aliyun;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.green20220302.Client;
import com.aliyun.green20220302.models.TextModerationPlusRequest;
import com.aliyun.green20220302.models.TextModerationPlusResponse;
import com.aliyun.green20220302.models.TextModerationPlusResponseBody;
import com.aliyun.teaopenapi.models.Config;
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

    public Map<String, String> scan(String content) throws Exception {
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

        Map<String, String> resultMap = new HashMap<>();

        try {
            TextModerationPlusResponse response = client.textModerationPlus(textModerationPlusRequest);
            if (response.getStatusCode() == 200) {
                TextModerationPlusResponseBody result = response.getBody();
                System.out.println(JSON.toJSONString(result));
                System.out.println("requestId = " + result.getRequestId());
                System.out.println("code = " + result.getCode());
                System.out.println("msg = " + result.getMessage());
                Integer code = result.getCode();
                if (200 == code) {
                    TextModerationPlusResponseBody.TextModerationPlusResponseBodyData data = result.getData();
                    System.out.println(JSON.toJSONString(data, true));

                    if (data != null){
                        //none, high
                        String riskLevel = data.riskLevel;
                        String description = "";
                        for (TextModerationPlusResponseBody.TextModerationPlusResponseBodyDataResult dataResult : data.result) {
                            description = dataResult.description;
                        }

                        resultMap.put("suggestion", description);
                        resultMap.put("label", riskLevel);
                    } else {
                        resultMap.put("suggestion", "pass");

                    }

                    return resultMap;

                } else {
                    System.out.println("text moderation not success. code:" + code);
                }
            } else {
                System.out.println("response not success. status:" + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }

}
