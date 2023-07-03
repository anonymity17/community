package com.majiang.demo.enums;

/**
 * 枚举评论的类型
 */
public enum NotificationtTypeEnum {
    REPLY_QUESTION(1,"回复了问题"),//当前评论时用来评论question的，是一级评论
    REPLY_COMMENT(2,"回复了评论");//当前评论时用来评论question下面的comment的，属于二级评论
    private int type;
    private String name;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    NotificationtTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * 根据传进来的枚举类型，输出对应的字符串
     * @param type
     * @return
     */
    public static String nameOfType(int type){
        for (NotificationtTypeEnum typeEnum : NotificationtTypeEnum.values()) {
            if (typeEnum.getType() == type){
                return typeEnum.getName();
            }
        }
        return "";

    }
}
