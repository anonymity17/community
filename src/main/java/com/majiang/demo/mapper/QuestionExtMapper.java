package com.majiang.demo.mapper;

import com.majiang.demo.model.Question;
import com.majiang.demo.model.QuestionExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * 这是一个额外的mapper，sql语句写在xml文件中
 * 自己写的额外的数据库操作，不会被Mybatis Generator替换掉
 */
public interface QuestionExtMapper {
    //更新阅读数
    int incView(Question record);
    //更新评论数（问题的评论数，question表中）
    int incCommentCount(Question record);
}