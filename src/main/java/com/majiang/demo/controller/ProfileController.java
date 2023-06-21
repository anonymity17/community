package com.majiang.demo.controller;

import com.majiang.demo.dto.PaginationDTO;
import com.majiang.demo.dto.QuestionDTO;
import com.majiang.demo.mapper.UserMapper;
import com.majiang.demo.model.Question;
import com.majiang.demo.model.User;
import com.majiang.demo.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ProfileController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;

    //通过控制器返回一个profile页面
    @GetMapping("/profile/{action}")//根据action的内容确定不同的页面
    public String profile(HttpServletRequest request,
                          @PathVariable(name = "action") String action,
                          @RequestParam(value = "page" ,defaultValue = "1") Integer page,
                          @RequestParam(value ="size", defaultValue = "5") Integer size,
                          Model model){
        //浏览器获取到cookie
        Cookie[] cookies = request.getCookies();
        User user = null;
        //遍历整个cookies数组，去找"token"(我们自己设置的那个，也就是与数据库中的相等)
        if (cookies != null && cookies.length != 0){
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())){
                    //找到了"token",要判断value是否和数据库中的相等
                    String token = cookie.getValue();//网页上的那个
                    //获取数据库中的token对应的value
                    user = userMapper.findByToken(token);
                    if (user != null){
                        //数据库中有这样一个用户，就将该用户放入会话中
                        request.getSession().setAttribute("user",user);
                        //会话中中的用户不为空才会展示”月牙“而不是”登录“
                    }
                    break;
                }
            }
        }
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
