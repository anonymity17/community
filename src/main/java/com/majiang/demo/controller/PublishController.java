package com.majiang.demo.controller;

import com.majiang.demo.dto.QuestionDTO;
import com.majiang.demo.mapper.QuestionMapper;
import com.majiang.demo.mapper.UserMapper;
import com.majiang.demo.model.Question;
import com.majiang.demo.model.User;
import com.majiang.demo.service.QuestionService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {
    @Autowired
    private QuestionService questionService;
//    @Autowired
//    private QuestionMapper questionMapper;//使用questionService来代替questionMapper
//    @Autowired
//    private UserMapper userMapper;
    @GetMapping("/publish")
    public String publish(){
        return "publish";
    }
    //前端get，就渲染页面，如上；前端post，就执行请求，如下

    @PostMapping("/publish")
    public String doPublish(@RequestParam(value = "title", required = false) String title,//required = false表示可以为空
                            @RequestParam(value = "description", required = false) String description,
                            @RequestParam(value = "tag", required = false) String tag,
                            @RequestParam(value = "id", required = false) Long id,//接收因为edit，前端传过来的id
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
        User user = (User) request.getSession().getAttribute("user");
        System.out.println("======="+user);
        if (user == null){
            model.addAttribute("error","用户未登录");
            return "publish";//有异常发生重新刷新该页面，还是停留在publish
        }
        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setId(id);
//        questionMapper.create(question);增加编辑功能后更新如下
        questionService.createOrUpdate(question);
        return "redirect:/";//没有异常跳转会首页
    }
    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id") Long id,
                       Model model){
        //通过id获取到一个question
        QuestionDTO question = questionService.getById(id);//将Mapper更改为service时，也将question更改为questionDTO，但是没关系，我们只需要拿出一些数据即可
        //将这个question回显到页面
        model.addAttribute("title", question.getTitle());
        model.addAttribute("description", question.getDescription());
        model.addAttribute("tag", question.getTag());
        model.addAttribute("id",question.getId());//如果是编辑则使用id；来标识
        //这个id并不需要回显到页面上，但是需要从页面传递会后端（要用来sql update)<input type="hidden" name="id" th:value="${id}">
        return "publish";//回显后跳到“发布”页面，所以“发布”页面也有两种可能：1）新增；2）更新
    }
}
