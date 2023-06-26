package com.majiang.demo.mapper;

import com.majiang.demo.model.Question;
import com.majiang.demo.model.QuestionExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface QuestionExtMapper {

    int incView(Question record);
}