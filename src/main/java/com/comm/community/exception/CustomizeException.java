package com.comm.community.exception;

public class CustomizeException extends RuntimeException{
    //为什么要extend RuntimeException：因为如果不继承RuntimeException，在需要抛出异常的地方需要加try-catch。要求在其他地方没有影响，仅仅在controller里try catch
    private String message;
    private Integer code;

    public CustomizeException(ICustomizeErrorCode errorCode){
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
