package com.heima.schedule.test;


import com.heima.common.redis.CacheService;
import com.heima.schedule.ScheduleApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CacheServiceTest {


    @Autowired
    private CacheService cacheService;

    @Test
    public void testList(){
//        cacheService.lLeftPush("lpush_key_1", "zhangsan");

        cacheService.lRightPop("lpush_key_1");

    }

    @Test
    public void testZSet(){
//        cacheService.zAdd("zset_key_1", "value_1", 100);
//        cacheService.zAdd("zset_key_1", "value_2", 500);
//        cacheService.zAdd("zset_key_1", "value_3", 300);
//        cacheService.zAdd("zset_key_1", "value_4", 1000);

//        String zsetKey1 = cacheService.getRange("zset_key_1", 0, 600);
        Set<String> set = cacheService.zRangeByScore("zset_key_1", 0, 600);
        System.out.println("set:" + set);

    }
}
