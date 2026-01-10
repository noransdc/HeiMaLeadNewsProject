package com.heima.wemedia;


import com.alibaba.fastjson.JSON;
import com.heima.common.constants.WmNewsMessageConstants;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class WmNewsTest {


    @Autowired
    private WmNewsService wmNewsService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    @Test
    public void testTaskFeign() {
        Integer id = 6281;
//        wmScheduleService.addNewsToTask(id, new Date());
    }

    @Test
    public void testDownOrUp() {
        WmNewsDto dto = new WmNewsDto();
        dto.setId(6302);
        dto.setEnable((short) 0);

        ResponseResult result = wmNewsService.downOrUp(dto);
        log.info("down or up, result:{}", result.getCode());
    }

    @Test
    public void testKafkaSend() {
        Map<String, Object> map = new HashMap<>();
        map.put("articleId", "wmNews.getArticleId()");
        map.put("enable", (short) 1);
        kafkaTemplate.send(WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC, JSON.toJSONString(map));
        log.info("kafka send:{}", JSON.toJSONString(map));
    }


}
