package com.majiang.demo.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode{
    QUESTION_NOT_FOUND("你找的问题不在了，要不要换一个试试？", 2001),
    TARGET_PARAM_NOT_FOUND("未选中任何问题的或评论进行回复", 2002),
    NO_LOGIN("当前操作需要登录，请登录后重试",2003),
    SYS_ERROR("服务器冒烟了，稍后再试哦",2004),
    TYPE_PARAM_WRONG("评论类型错误或不存在",2005),
    COMMENT_NOT_FOUND("你找的评论不在了，要不要换一个试试？",2006),
    CONTENT_IS_EMPTY("输入内容不能为空",2007),
    ;
    private String message;
    private Integer code;
    CustomizeErrorCode(String message,Integer code) {
        this.message = message;
        this.code = code;
    }

    @Override
    public String getMessage(){
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }


}
