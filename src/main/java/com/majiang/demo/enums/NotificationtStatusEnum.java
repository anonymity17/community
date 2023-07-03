package com.majiang.demo.enums;

/**
 * 枚举评论的类型
 */
public enum NotificationtStatusEnum {
    UNREAD(0),
    READ(1),/*该通知被已读*/
    ;
    private int status;
    public int getStatus(){
        return status;
    }

    NotificationtStatusEnum(int status) {
        this.status = status;
    }
}
