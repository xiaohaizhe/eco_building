package com.giot.eco_building.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum HttpResponseStatusEnum implements CommonResponse {

    SUCCESS(0, "success"),                            // 成功请求
    FAILURE(1, "failure"),                            // 失败请求
    FORBIDDEN_OPERATION(2, "forbidden"),               // 权限不足
    EXCEPTION(100, "exception"),                         //异常

    USER_HAS_EXISTED(10001, "用户已存在"),
    USER_NOT_EXISTED(10002, "用户不存在"),
    USERNAME_NOT_EXISTED(10003, "用户名不存在"),
    ;
    private Integer code;
    private String message;

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Object getResult() {
        return null;
    }

}
