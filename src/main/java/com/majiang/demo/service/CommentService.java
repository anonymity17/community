package com.majiang.demo.service;

import com.majiang.demo.dto.CommentDTO;
import com.majiang.demo.enums.CommentTypeEnum;
import com.majiang.demo.exception.CustomizeErrorCode;
import com.majiang.demo.exception.CustomizeException;
import com.majiang.demo.mapper.CommentMapper;
import com.majiang.demo.mapper.QuestionExtMapper;
import com.majiang.demo.mapper.QuestionMapper;
import com.majiang.demo.mapper.UserMapper;
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

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;

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

    public List<CommentDTO> listByQuestionId(Long id) {
        CommentExample example = new CommentExample();
        example.createCriteria()
                        .andParentIdEqualTo(id) // 不只有parentId
                .andTypeEqualTo(CommentTypeEnum.QUESTION.getType());//必须是问题下面的评论列表
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
