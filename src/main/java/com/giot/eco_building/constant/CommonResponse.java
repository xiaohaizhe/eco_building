package com.giot.eco_building.constant;

/**
 * 通用响应体
 */
public interface CommonResponse {

    /**
     * 获取状态码
     */
    Integer getCode();

    /**
     * 获取消息
     */
    String getMessage();

    /**
     * 响应结果
     */
    Object getResult();

}
