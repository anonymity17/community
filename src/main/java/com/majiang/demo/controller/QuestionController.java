package com.majiang.demo.controller;

import com.majiang.demo.dto.QuestionDTO;
import com.majiang.demo.mapper.QuestionMapper;
import com.majiang.demo.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @GetMapping("/question/{id}")//这个{}里面的id可以通过下面这个@PathVariable拿到id
    public String question(@PathVariable(name = "id") Integer id,
                           Model model){
//        通过id获取到我的问题列表中 的某一个问题
//        已经存在service，controller应该调用service，service去调用mapper
        QuestionDTO questionDTO = questionService.getById(id);

        //累加阅读数
        //上面已经判断过id是否存在，所以在invView方法中不需要再次判断id是否存在
        questionService.incView(id);
//        要将获取到的问题展示在页面上=》使用model
        model.addAttribute("question",questionDTO);
        return "question";
    }
}
