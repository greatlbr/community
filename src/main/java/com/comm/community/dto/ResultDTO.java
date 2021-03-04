package com.comm.community.dto;

import com.comm.community.exception.CustomizeErrorCode;
import com.comm.community.exception.CustomizeException;
import lombok.Data;

import java.util.List;

@Data
public class ResultDTO<T> {//错误信息全都封装在枚举类，增加可重用性  //ResultDTO是为json传输服务的
    private Integer code;
    private String message;
    private T data;//因为不确定传过来的是什么类型参数，比如有可能是User或者List(返回多个对象)，所以使用泛型

    public static ResultDTO errorOf(Integer code, String message){//可以理解为一个构造方法 //静态工厂模式？
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(code);
        resultDTO.setMessage(message);
        return resultDTO;
    }

    public static ResultDTO errorOf(CustomizeErrorCode errorCode) {
        return errorOf(errorCode.getCode(),errorCode.getMessage());
    }

    public static ResultDTO errorOf(CustomizeException e) {
        return errorOf(e.getCode(),e.getMessage());
    }

    public static ResultDTO okOf(){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求收到");
        return resultDTO;
    }

    public static <T> ResultDTO okOf(T t){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        resultDTO.setData(t);
        return resultDTO;
    }

}
