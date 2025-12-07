package com.heima.wemedia;


import com.heima.wemedia.service.WmScheduleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class WmNewsTest {

    @Autowired
    private WmScheduleService wmScheduleService;

    @Test
    public void testTaskFeign(){
        Integer id = 6281;
        wmScheduleService.addNewsToTask(id, new Date());
    }

}
