package com.pyx.community.exception;

/**
 * 这是自定义的异常类。
 * 必须继承Throwable或者RuntimeException
 * 这里选择RuntimeException，防止其他类抛出异常时trycatch
 */
public class CustomizeException extends RuntimeException {
    private String message;
    private Integer code;

    public CustomizeException(ICustomizeErrorCode errorCode){
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
