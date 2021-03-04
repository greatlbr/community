package com.comm.community.enums;

public enum NotificationEnum {
    REPLY_QUESTION(1,"回复了问题"),//用于在数据库中映射出来的展示(拼接评论人/回复的人和回复的话题),原因是让数据库尽量那个存少的数据
    REPLY_COMMENT(1,"回复了评论");

    //因为有回复有评论有点赞，所以新建一个enum
    private int type;//0表示未读，1表示已读
    private String name;

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }



    NotificationEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }
}
