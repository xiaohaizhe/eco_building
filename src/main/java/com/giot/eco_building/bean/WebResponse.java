package com.giot.eco_building.bean;

import com.giot.eco_building.constant.CommonResponse;
import com.giot.eco_building.constant.HttpResponseStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: pyt
 * @Date: 2020/6/9 10:32
 * @Description: HTTP 统一响应结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebResponse {

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 结果
     */
    private Object result;

    public WebResponse(HttpResponseStatusEnum httpResponseStatusEnum) {
        this.code = httpResponseStatusEnum.getCode();
        this.message = httpResponseStatusEnum.getMessage();
    }

    public WebResponse(CommonResponse commonResponse) {
        this.code = commonResponse.getCode();
        this.message = commonResponse.getMessage();
    }

    /**
     * 成功响应
     */
    public static WebResponse success() {
        return new WebResponse(HttpResponseStatusEnum.SUCCESS.getCode(), HttpResponseStatusEnum.SUCCESS.getMessage(), null);
    }

    /**
     * 成功响应
     */
    public static WebResponse success(Object result) {
        return new WebResponse(HttpResponseStatusEnum.SUCCESS.getCode(), HttpResponseStatusEnum.SUCCESS.getMessage(), result);
    }

    /**
     * 响应失败
     * @param
     * @return
     */
    public static WebResponse failure() {
        return new WebResponse(HttpResponseStatusEnum.FAILURE.getCode(),HttpResponseStatusEnum.FAILURE.getMessage(), null);
    }

    /**
     * 响应失败
     * @param response
     * @return
     */
    public static WebResponse failure(HttpResponseStatusEnum response) {
        return new WebResponse(response.getCode(), response.getMessage(), null);
    }

    /**
     * 响应异常
     * @param e
     * @return
     */
    public static WebResponse exception(Exception e) {
        return new WebResponse(HttpResponseStatusEnum.EXCEPTION.getCode(), e.getMessage(), null);
    }

    /**
     * 禁止操作
     */
    public static WebResponse forbidden() {
        return new WebResponse(HttpResponseStatusEnum.FORBIDDEN_OPERATION.getCode(), HttpResponseStatusEnum.FORBIDDEN_OPERATION.getMessage(), null);
    }

}

