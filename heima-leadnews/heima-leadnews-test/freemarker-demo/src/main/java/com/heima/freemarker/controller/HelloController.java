package com.heima.freemarker.controller;


import com.heima.freemarker.entity.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;


@Controller
public class HelloController {

    @GetMapping("/basic")
    public String hello(Model model){
        model.addAttribute("name", "freemarker");

        Student student = new Student();
        student.setName("zhang san");
        student.setAge(18);

        model.addAttribute("stu", student);

        return "01-basic";
    }


    @GetMapping("/list")
    public String list(Model model){
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

        model.addAttribute("stus", list);

        Map<String, Student> map = new HashMap<>();
        map.put("stu0", student0);
        map.put("stu1", student1);

        model.addAttribute("stuMap", map);

        //

        return "02-list";
    }


}
