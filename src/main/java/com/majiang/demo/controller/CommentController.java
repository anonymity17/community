package com.majiang.demo.controller;

import com.majiang.demo.dto.CommentCreateDTO;
import com.majiang.demo.dto.CommentDTO;
import com.majiang.demo.dto.ResultDTO;
import com.majiang.demo.enums.CommentTypeEnum;
import com.majiang.demo.exception.CustomizeErrorCode;
import com.majiang.demo.model.Comment;
import com.majiang.demo.model.User;
import com.majiang.demo.service.CommentService;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**在controller捕获异常的方式：
 * 1，使用 try-catch 块捕获异常：
 * 2,在方法上使用 @ExceptionHandler 注解捕获特定的异常
 * 本控制器是将异常信息封装后发送到前端,不会被拦截器拦截
 *
 * service中的异常都向上抛了，被异常拦截器捕获了（因为没有在controller捕获），异常相关信息被放入model，弹出error页面
 * controller将异常分装成ResultDTO对象，并且使用@ResponseBody 修饰方法，方法返回的也是ResultDTO对象，因此是将异常信息通过@ResponseBody 序列化成json发送到前端
 * 前端使用js中的post()中response接收到异常信息，以alert弹窗的形式表现
 */
@Controller
public class CommentController {
//    @Autowired
//    private CommentMapper commentMapper;
//不仅需要CommentMapper还需要QuestionMapper=》引入CommentService
    @Autowired
    private CommentService commentService;


    //使用json传输，前后端就能使用同一套体系
    @ResponseBody //将对象（当前方法的返回值）自动序列化成json，发送到前端（{"message":"success"}）
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request){//传入的是json的格式，包装成commentDTO对象再将对象中的内容通过comment插入数据库
        User user = (User) request.getSession().getAttribute("user");
        if (user == null){
            //NO_LOGIN("当前操作需要登录，请登录后重试",2003)
            //传进去code，message，然后使用ResultDTO封装成对象
            //这个对象会通过ResponseBody序列化成json，发送到前端,js，的post()方法中的response会获取到这些信息
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);//NO_LOGIN("当前操作需要登录，请登录后重试",2003)
        }
        //评论内容不能为空的判断
        if (commentCreateDTO == null || StringUtils.isNullOrEmpty(commentCreateDTO.getContent())){//isBlank()没找到
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
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
        comment.setCommentator(user.getId());//session里面的user
        //默认值总是不能设置为0，导致一直未空异常
        comment.setLikeCount(0L);
        comment.setCommentCount(0);
//        commentMapper.insert(comment);
        commentService.insert(comment, user);
        return ResultDTO.okOf();
    }
    @ResponseBody
    @RequestMapping(value = "/comment/{id}",method = RequestMethod.GET)
    public ResultDTO<List> comment(@PathVariable(name = "id") Long id){
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.okOf(commentDTOS);//此处想传递一个评论列表回去，而不只是简单的code和message=》去ResultDTO中添加
    }
}
