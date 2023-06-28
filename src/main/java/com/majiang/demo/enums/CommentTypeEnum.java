package com.majiang.demo.enums;

/**
 * 枚举评论的类型
 */
public enum CommentTypeEnum {
    QUESTION(1),//当前评论时用来评论question的，是一级评论
    COMMENT(2);//当前评论时用来评论question下面的comment的，属于二级评论

    private Integer type;

    /**
     * 评论的类型是否存在
     * @param type
     * @return
     */
    public static boolean isExist(Integer type) {
        for (CommentTypeEnum typeEnum : CommentTypeEnum.values()) {
            if (typeEnum.getType() == type){
                return true;
            }
        }
        return false;
    }

    public Integer getType() {
        return type;
    }

    CommentTypeEnum(Integer type) {
        this.type = type;
    }
}
