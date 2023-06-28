package com.majiang.demo.dto;

import lombok.Data;

//自动实现json的反序列化
//这个DTO用于来创建评论的时候使用，与呈现问题下面评论列表不同
@Data
public class CommentCreateDTO {
    private Long parentId;
    private String content;
    private Integer type;
}
