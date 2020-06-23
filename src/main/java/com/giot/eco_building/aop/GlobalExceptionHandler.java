package com.giot.eco_building.aop;

import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.exception.TestIOException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * @Author: pyt
 * @Date: 2020/6/11 10:55
 * @Description:
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public WebResponse handle(Exception e) {
        if (e instanceof TestIOException) {
            e = (TestIOException) e;
        }
        return WebResponse.exception(e);
    }

    @ExceptionHandler(value = IOException.class)
    public WebResponse handle(IOException e) {
        return WebResponse.exception(e);
    }
}
