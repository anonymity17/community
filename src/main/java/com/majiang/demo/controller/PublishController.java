package com.majiang.demo.controller;

import com.majiang.demo.mapper.QuestionMapper;
import com.majiang.demo.mapper.UserMapper;
import com.majiang.demo.model.Question;
import com.majiang.demo.model.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;
    @GetMapping("/publish")
    public String publish(){
        return "publish";
    }
    //前端get，就渲染页面，如上；前端post，就执行请求，如下

    @PostMapping("/publish")
    public String doPublish(@Param("title") String title,
                            @Param("description") String description,
                            @Param("tag") String tag,
                            HttpServletRequest request,
                            Model model){
        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        if (title == null || title == ""){
            model.addAttribute("error","标题不能为空");
            return "publish";
        }
        if (description == null || description == ""){
            model.addAttribute("error","描述不能为空");
            return "publish";
        }
        if (tag == null || tag == ""){
            model.addAttribute("error","标签不能为空");
            return "publish";
        }
        //在发布的时候也要做判断用户是否登录
        // 获取user
        User user = null;
        Cookie[] cookies = request.getCookies();
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
//            model里面的东西可以直接在页面上获取到
            model.addAttribute("error","用户未登录");
            return "publish";//有异常发生重新刷新该页面，还是停留在publish
        }

        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setGmtCreate(System.currentTimeMillis());
        question.setGmtModified(question.getGmtCreate());
        questionMapper.create(question);
        return "redirect:/";//没有异常跳转会首页

    }
}
