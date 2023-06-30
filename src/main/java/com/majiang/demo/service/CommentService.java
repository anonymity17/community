package com.majiang.demo.service;

import com.majiang.demo.dto.CommentDTO;
import com.majiang.demo.enums.CommentTypeEnum;
import com.majiang.demo.exception.CustomizeErrorCode;
import com.majiang.demo.exception.CustomizeException;
import com.majiang.demo.mapper.*;
import com.majiang.demo.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * service中的异常都向上抛了，被异常拦截器捕获了（因为没有在controller捕获），异常相关信息被放入model，弹出error页面
 * controller将异常分装成ResultDTO对象，并且使用@ResponseBody 修饰方法，方法返回的也是ResultDTO对象，因此是将异常信息通过@ResponseBody 序列化成json发送到前端
 * 前端使用js中的post()中response接收到异常信息，以alert弹窗的形式表现
 */
@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private CommentExtMapper commentExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Transactional //整个方法都作为一个事务
    public void insert(Comment comment) {
        if (comment.getParentId() == null || comment.getParentId() == 0){
            //评论的父亲（也就是评论所属的问题）都不存在，评论自然不存在
            //“评论不存在”此时已经在业务层，要将该消息传到控制层
            //使用Exception抛出去
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())){
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if (comment.getType() == CommentTypeEnum.COMMENT.getType()){
            //当前的评论是评论，所以是用来回复评论的评论
            //根据当前评论的父亲找到被评论的评论
            //在comment表中查找
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment.getId() == null){//被评论的那个评论没有了
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            //可以正常评论，就是将当前这个评论存储数据库
            commentMapper.insert(comment);
            /*上面这个comment 是将二级评论插入评论表中，下面增加数量是对一级评论增加*/
            /*增加评论数*/
            Comment parentComment = new Comment();/*更具有一级评论对象更新一级评论中的评论数量*/
            parentComment.setId(comment.getParentId());/*设置id确定一个一级评论对象，将该对象的commentCount=1，作为数量增加步长*/
            parentComment.setCommentCount(1);
            commentExtMapper.incCommentCount(parentComment);
        }else{
            //上方已经做好了校验，此处评论评论的是问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());//被评论的问题
            if (question == null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            question.setCommentCount(1);//设置每次只增加一
            questionExtMapper.incCommentCount(question);//更新question表中的comment_count
            //回复问题
        }
    }

    /**
     * 根据枚举的类型决定（1）问题下面的评论列表；（2）评论下面的评论列表
     * 根据id决定 哪条问题（评论）下的评论列表
     * @param id
     * @param type
     * @return
     */
    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {
        CommentExample example = new CommentExample();
        example.createCriteria()
                        .andParentIdEqualTo(id) // 不只有parentId
                .andTypeEqualTo(type.getType());//必须是问题下面的评论列表
        example.setOrderByClause("gmt_create desc");//使评论列表按照时间顺序倒序
        List<Comment> comments = commentMapper.selectByExample(example);
        //获取每个评论的用户，即comment中的commentator信息
        //判断是否存在评论列表
        if (comments.size() == 0){
            return new ArrayList<>();//空列表
        }

        //遍历comments，遍历每一个comment，返回每一个comment的commentator,如下
        //comments.stream().map(comment -> comment.getCommentator())
        //将获取的commentator返回为一个list对象：.collect(Collectors.toList());
        //commentators为所有的评论者的id，为防止重复list改为set
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        //更改为list，因为后面目前只能传入list而不能是set
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(commentators);

        //根据id获取全部的user列表
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);

        //将user和comment对应起来
        //如果暴力做法=》双层循环=》现将userId和user用map保存
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        //依然使用stream去生成commentDTO列表
        List<CommentDTO> commentDTOs = comments.stream().map(comment -> {
            //遍历到的每一个评论comment，生成新的commentDTO
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));//根据userMap中的id能够很快获取到user对象
            //完成commentDTO的构建后返回
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOs;
    }
}
