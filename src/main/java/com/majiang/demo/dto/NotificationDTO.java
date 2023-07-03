package com.majiang.demo.dto;

import com.majiang.demo.model.User;
import lombok.Data;

/**
 * 目前假设这个通知的用户名以及相关问题的标题不可变
 */
@Data
public class NotificationDTO {/*只多了一个typeName
*/
    private Long id;
    private Long gmtCreate;
    private Integer status;
    private Long notifier;
    private Long outerId; //被回复的那个问题（评论）的id
    private String notifierName;//回复你的那用户的名字
    private String outerTitle;//被回复的标题
    private String typeName;/*表示“回复了问题” 或者 “回复了评论” 这两个类型*/
    private Integer type;
}
