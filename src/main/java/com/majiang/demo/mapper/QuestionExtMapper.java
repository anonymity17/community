package com.majiang.demo.mapper;

import com.majiang.demo.dto.QuestionQueryDTO;
import com.majiang.demo.model.Question;

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
    /*更加一条记录的tag去匹配其他相似的tag的记录*/
    List<Question> selectRelated(Question question);

    /**
     * 根据查询条件展示问题列表
     * @param questionQueryDTO 这里的查询条件：搜索框内容，页面大小（设置一页展示记录条数），第几页（每一页不同啊）
     * @return
     */
    int countBySearch(QuestionQueryDTO questionQueryDTO);

    /**
     * 根据搜索框，偏移量，页面大小，查询问题列表
     * 可以看出这三个条件多次使用，直接包装成QuestionQueryDTO了，很方便
     * @param questionQueryDTO
     * @return
     */
    List<Question> selectBySearch(QuestionQueryDTO questionQueryDTO);
}