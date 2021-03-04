package com.comm.community.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode{//定义接口形式是防止类爆炸
    QUESTION_NOT_FOUND(2001,"question error,msg"),
    TARGET_PARAM_NOT_FOUND(2002,"未选中问题或评论进行回复,问题可能已删除"),
    NO_LOGIN(2003,"当前操作需要登陆，请登陆后重试"),
    SYS_ERROR(2004,"服务器异常"),
    TYPE_PARAM_WRONG(2005,"评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006,"回复的评论error"),
    CONTENT_IS_EMPTY(2007,"输入内容不能为空"),
    READ_NOTIFICATION_FAIL(2008,"当前读取的是别的用户的信息"),
    NOTIFICATION_NOT_FOUND(2009,"通知找不到"),
    FILE_UPLOAD_FAIL(2010,"图片上传error");
    @Override
    public String getMessage(){
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }


    private Integer code;
    private String message;

    CustomizeErrorCode(Integer code, String message){
        this.message = message;
        this.code = code;
    }
}
