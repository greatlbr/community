package com.comm.community.enums;

public enum NotificationStatusEnum {//创建这个枚举是因为：为了以后重构的时候能发现1和0是干什么的
    UNREAD(0),READ(1);
    private int status;

    public int getStatus() {
        return status;
    }

    NotificationStatusEnum(int status) {
        this.status = status;
    }
}
