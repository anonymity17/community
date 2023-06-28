package com.majiang.demo.controller;

import com.majiang.demo.dto.CommentCreateDTO;
import com.majiang.demo.dto.ResultDTO;
import com.majiang.demo.exception.CustomizeErrorCode;
import com.majiang.demo.model.Comment;
import com.majiang.demo.model.User;
import com.majiang.demo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CommentController {
//    @Autowired
//    private CommentMapper commentMapper;
//不仅需要CommentMapper还需要QuestionMapper=》引入CommentService
    @Autowired
    private CommentService commentService;


    //使用json传输，前后端就能使用同一套体系
    @ResponseBody //将对象（objectHashMap）自动序列化成json，发送到前端（{"message":"success"}）
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request){//传入的是json的格式，包装成commentDTO对象再将对象中的内容通过comment插入数据库
        User user = (User) request.getSession().getAttribute("user");
        if (user == null){
            //NO_LOGIN("当前操作需要登录，请登录后重试",2003)
            //传进去code，message，然后使用ResultDTO封装成对象
            //这个对象会通过ResponseBody序列化成json，发送到前端
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);//NO_LOGIN("当前操作需要登录，请登录后重试",2003)
        }
        //question.html按钮onclick
        // =>communit.js中的post()
        // =>post()将页面的数据对象转化成字符串json，再由url链接到这里
        // =>@RequestBody反序列化成对象comment
        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());

        //type是评论的时候查评论，是问题的时候查问题，使用枚举表示
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setCommentator(22L);//session里面的user
        //默认值总是不能设置为0，导致一直未空异常
        comment.setLikeCount(0L);
//        commentMapper.insert(comment);
        commentService.insert(comment);
        return ResultDTO.okOf();
    }
}
