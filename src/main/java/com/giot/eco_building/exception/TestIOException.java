package com.giot.eco_building.exception;

/**
 * @Author: pyt
 * @Date: 2020/6/16 10:32
 * @Description:
 */
public class TestIOException extends RuntimeException{
    public TestIOException() {
        super();
    }

    public TestIOException(String message) {
        super(message);
    }

    public TestIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestIOException(Throwable cause) {
        super(cause);
    }

    protected TestIOException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
