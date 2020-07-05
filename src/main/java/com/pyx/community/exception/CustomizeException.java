package com.pyx.community.exception;

/**
 * 这是自定义的异常类。
 * 必须继承Throwable或者RuntimeException
 * 这里选择RuntimeException，防止其他类抛出异常时trycatch
 */
public class CustomizeException extends RuntimeException {
    private String message;

    public CustomizeException(ICustomizeErrorCode errorCode){
        this.message = errorCode.getMessage();
    }

    public CustomizeException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
