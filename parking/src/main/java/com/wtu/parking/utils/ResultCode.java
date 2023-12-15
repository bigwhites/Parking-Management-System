package com.wtu.parking.utils;

public enum ResultCode{
    SUCCESS(200,"成功"),
    USER_NOT_EXIST(2000,"无该用户"),
    PARAM_FORMAT_WRONG(2001,"请求参数格式错误"),
    USER_BEEN_REG(2002,"已被注册"),
    PWD_ERR(2003,"密码错误"),
    NO_SUCH_REC(3001,"没有记录"),
    CAR_IN_LOT(3002,"车辆已经在场内");

    private Integer code;
    private String message;

    ResultCode(Integer code,String message){
        this.code=code;
        this.message=message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


}
