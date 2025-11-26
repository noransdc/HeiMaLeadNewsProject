package com.heima.wemedia;

import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenImageScanV2;
import com.heima.common.aliyun.GreenTextScan;
import com.heima.common.aliyun.GreenTextScanV1;
import com.heima.file.service.FileStorageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
public class AliyunTest {

    @Autowired
    private GreenTextScan greenTextScan;

    @Autowired
    private GreenImageScan greenImageScan;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private GreenTextScanV1 greenTextScanV1;

    @Autowired
    private GreenImageScanV2 greenImageScanV2;

    @Test
    public void testScanText() throws Exception {
//        Map map = greenTextScan.greeTextScan("我是一个好人");
//        System.out.println(map);

        Map<String, String> map = greenTextScanV1.scan("我是一个好人");
        System.out.println(map);
    }

    @Test
    public void testScanImages() throws Exception {
//        String url = "http://192.168.238.101:9000/leadnews/2025/11/22/e34ad419f3ad4ac8a2e5cd1745999850.jpg";
//        byte[] bytes = fileStorageService.downLoadFile(url);
//        List<byte[]> images = new ArrayList<>();
//        images.add(bytes);
//        Map map = greenImageScan.imageScan(images);
//        System.out.println(map);

        Map<String, String> map = greenImageScanV2.scan(Collections.emptyList());
        System.out.println(map);

    }

}
