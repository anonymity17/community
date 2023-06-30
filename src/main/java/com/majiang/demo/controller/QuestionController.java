package com.majiang.demo.controller;

import com.majiang.demo.dto.CommentDTO;
import com.majiang.demo.dto.QuestionDTO;
import com.majiang.demo.enums.CommentTypeEnum;
import com.majiang.demo.service.CommentService;
import com.majiang.demo.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")//这个{}里面的id可以通过下面这个@PathVariable拿到id
    public String question(@PathVariable(name = "id") Long id,
                           Model model){
//        通过id获取到我的问题列表中 的某一个问题
//        已经存在service，controller应该调用service，service去调用mapper
        QuestionDTO questionDTO = questionService.getById(id);
        // 标签匹配到的其他问题列表
        List<QuestionDTO> relatedQuestions = questionService.selectRelated(questionDTO);
        //获取当前问题所有的评论列表
        List<CommentDTO> comments = commentService.listByTargetId(id, CommentTypeEnum.QUESTION);
        //累加阅读数
        //上面已经判断过id是否存在，所以在invView方法中不需要再次判断id是否存在
        questionService.incView(id);
//        要将获取到的问题展示在页面上=》使用model
        model.addAttribute("question",questionDTO);
        model.addAttribute("comments",comments);
        model.addAttribute("relatedQuestions",relatedQuestions);
        return "question";
    }
}
