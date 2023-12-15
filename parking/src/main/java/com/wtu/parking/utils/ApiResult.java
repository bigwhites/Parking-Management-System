package com.wtu.parking.utils;

import lombok.Data;
import java.io.Serializable;

@Data
public class ApiResult implements Serializable {
    String message;
    Integer code;
    Object data;


    private ApiResult( Integer code, String message,Object data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    private ApiResult(ResultCode resultCode,Object data){
        this.data=data;
        this.message=resultCode.getMessage();
        this.code=resultCode.getCode();
    }

    public static ApiResult success(Object data){
        return new ApiResult(ResultCode.SUCCESS,data);
    }
    public static ApiResult failure(ResultCode resultCode,Object data){
        return new ApiResult(resultCode,data);
    }

    public static ApiResult success(int code,String message,Object data){
        return new ApiResult(code,message,data);
    }

    public static ApiResult failure(int code,String message,Object data){
        return new ApiResult(code,message,data);
    }
}
