package com.majiang.demo.dto;

import com.majiang.demo.model.User;
import lombok.Data;

//这个DTO用于呈现问题下面评论列表
@Data
public class CommentDTO {

    private Long id;
    private Long parentId;
    private String content;
    private Integer type;
    private Long commentator;
    private Long gmtCreate;
    private Long gmtModified;
    private Long likeCount;
    private User user;
}
