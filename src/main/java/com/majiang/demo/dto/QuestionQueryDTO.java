package com.majiang.demo.dto;

import lombok.Data;

@Data
public class QuestionQueryDTO {/*存在搜索后查询问题列表所需要的数据*/
    private String search;
    private Integer page;/*偏移量offset，也就是第一页*/
    private Integer size;
}
