package com.majiang.demo.mapper;

import com.majiang.demo.model.Comment;

/**
 * 这是一个额外的mapper，sql语句写在xml文件中
 * 自己写的额外的数据库操作，不会被Mybatis Generator替换掉
 */
public interface CommentExtMapper {
    //更新评论数（问题的评论数，question表中）
    int incCommentCount(Comment record);
}