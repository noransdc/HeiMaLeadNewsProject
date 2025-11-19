package com.heima.freemarker.test;


import com.heima.freemarker.entity.Student;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileWriter;
import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FreemarkerTest {

    @Autowired
    private Configuration configuration;

    @Test
    public void test(){
        try {
            Template template = configuration.getTemplate("02-list.ftl");
            template.process(getData(), new FileWriter("d:/list.html"));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Map<String, Object> getData(){
        Map<String, Object> dataMap = new HashMap<>();

        Student student0 = new Student();
        student0.setName("zhangSan");
        student0.setAge(18);
        student0.setMoney(100f);

        Student student1 = new Student();
        student1.setName("liSi");
        student1.setAge(28);
        student1.setMoney(220f);

        List<Student> list = new ArrayList<>();
        list.add(student0);
        list.add(student1);

        dataMap.put("stus", list);

        Map<String, Student> stuMap = new HashMap<>();
        stuMap.put("stu0", student0);
        stuMap.put("stu1", student1);

        dataMap.put("stuMap", stuMap);

        return dataMap;
    }


}
