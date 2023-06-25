package com.majiang.demo.controller;

import com.majiang.demo.dto.PaginationDTO;
import com.majiang.demo.model.User;
import com.majiang.demo.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {
    @Autowired
    private QuestionService questionService;

    //通过控制器返回一个profile页面
    @GetMapping("/profile/{action}")//根据action的内容确定不同的页面
    public String profile(HttpServletRequest request,
                          @PathVariable(name = "action") String action,
                          @RequestParam(value = "page" ,defaultValue = "1") Integer page,
                          @RequestParam(value ="size", defaultValue = "5") Integer size,
                          Model model){
        User user = (User) request.getSession().getAttribute("user");
        if (user == null){
            model.addAttribute("error","用户未登录");
            return "redirect:/";
        }

        if ("questions".equals(action)){
            //action=questions，即/profile/questions
            //表示访问的页面“我的问题”
            //为了访问该页面，现将这个页面的标识保存起来
            model.addAttribute("section","questions");//1.页面的路径，作为section保存
            model.addAttribute("sectionName","我的提问");//2.该路径下的页面的标题
            //将这两个参数映射到页面中
        }else if ("replies".equals(action)){
            model.addAttribute("section","replies");//1.页面的路径，作为section保存
            model.addAttribute("sectionName","最新回复");//2.该路径下的页面的标题
        }

        //向list中传入用户的id，就可以获取到自己所提的所有问题
        PaginationDTO paginationDTOS = questionService.list(user.getId(),page,size);
        model.addAttribute("pagination",paginationDTOS);
        return "profile";
    }
}
