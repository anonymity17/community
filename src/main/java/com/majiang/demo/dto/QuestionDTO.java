package com.majiang.demo.dto;

import com.majiang.demo.model.User;
import lombok.Data;

/**
 * 与question中的属性都是一样的，就是多了一个User属性
 * 因为question表中没有User
 */
@Data
public class QuestionDTO {
    private Long id;
    private String title;
    private String description;
    private String tag;
    private Long creator;
    private Long gmtCreate;
    private Long gmtModified;
    private Integer commentCount;
    private Integer viewCount;
    private Integer likeCount;
    private User user;
}
