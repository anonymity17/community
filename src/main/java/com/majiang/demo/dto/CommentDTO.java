package com.majiang.demo.dto;

import lombok.Data;

//自动实现json的反序列化
@Data
public class CommentDTO {
    private Long parentId;
    private String content;
    private Integer type;
}
