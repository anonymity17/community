package com.majiang.demo.service;

import com.majiang.demo.enums.CommentTypeEnum;
import com.majiang.demo.exception.CustomizeErrorCode;
import com.majiang.demo.exception.CustomizeException;
import com.majiang.demo.mapper.CommentMapper;
import com.majiang.demo.mapper.QuestionExtMapper;
import com.majiang.demo.mapper.QuestionMapper;
import com.majiang.demo.model.Comment;
import com.majiang.demo.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;
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
            if (question.getId() == null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            question.setCommentCount(1);//设置每次只增加一
            questionExtMapper.incCommentCount(question);//更新question表中的comment_count

            //回复问题
        }
    }
}
