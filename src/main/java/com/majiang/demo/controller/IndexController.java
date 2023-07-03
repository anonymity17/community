package com.majiang.demo.controller;

import com.majiang.demo.dto.PaginationDTO;
import com.majiang.demo.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 验证cookie中的token与数据库中的token
 */
@Controller
public class IndexController {
    @Autowired
    private QuestionService questionService;

    @GetMapping("/")//访问首页的时候，如下
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "0") Integer page,
                        @RequestParam(name = "size", defaultValue = "5") Integer size,
                        @RequestParam(name = "search", required = false) String search){/*搜索框中的内容*/
//        第一次登录的时候就将生成的token放入的cookie，如果该用户没有登录过自然是不存在这样一个token
        //拿不到当然就是登录不成功的

        //获取到question表中的数据 ，将其 放入model展示到index页面
//        List<QuestionDTO> questionList = questionService.list(page, size);
        PaginationDTO pagination = questionService.list(search, page, size);
        //将数据放到model中
//        model.addAttribute("questions",questionList);//页面中使用的名字"questions"
        model.addAttribute("pagination",pagination);
        model.addAttribute("search",search);/*搜索以后的分页需要这个*/
        return  "index";
    }
}
