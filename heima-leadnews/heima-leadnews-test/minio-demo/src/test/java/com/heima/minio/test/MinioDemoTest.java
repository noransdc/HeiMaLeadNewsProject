package com.heima.minio.test;


import com.heima.file.service.FileStorageService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MinioDemoTest {

    @Autowired
    private FileStorageService fileStorageService;

    @Test
    public void uploadHtml(){
        try {
            FileInputStream fileInputStream = new FileInputStream("D:\\list.html");

            MinioClient minioClient = MinioClient.builder()
                    .credentials("minio", "minio123")
                    .endpoint("http://192.168.238.101:9000")
                    .build();

            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object("list.html")
                    .contentType("text/html")
                    .bucket("leadnews")
                    .stream(fileInputStream, fileInputStream.available(), -1)
                    .build();

            minioClient.putObject(putObjectArgs);

            System.out.println("http://192.168.238.101:9000/leadnews/list.html");

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void updateByMinioStarter(){
        try {
            FileInputStream fileInputStream = new FileInputStream("D:\\list.html");
            String path = fileStorageService.uploadHtmlFile("", "list1.html", fileInputStream);

            System.out.println(path);

        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
