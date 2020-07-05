package com.pyx.community.exception;

/**
 * 枚举类型。将错误信息封装到枚举类，层层包装
 * 解耦合，代码中可能会出现很多相同的异常，封装到枚举就是解耦
 */
public enum CustomizeErrorCode implements ICustomizeErrorCode{
    QUESTION_NOT_FOUND("您访问的问题不存在，要不要换个试试");
    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    CustomizeErrorCode(String message) {
        this.message = message;
    }
}
