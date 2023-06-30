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
    /*将question这个对象对应的表中的一列数据中的comment_count + 1
    * 这个对象就是对应数据表中的一条记录
    * 而数据表中那么多记录，如何确定是哪一条就是根据该对象的id
    * 所以在自增的时候要创建这样一个对象至少要对id和commentCount属性赋值*/
    int incCommentCount(Question record);
}