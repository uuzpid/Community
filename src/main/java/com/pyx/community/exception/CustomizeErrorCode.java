package com.pyx.community.exception;

/**
 * 枚举类型。将错误信息封装到枚举类，层层包装
 * 解耦合，代码中可能会出现很多相同的异常，封装到枚举就是解耦
 */
public enum CustomizeErrorCode implements ICustomizeErrorCode{
    QUESTION_NOT_FOUND(2001, "您访问的问题不存在，要不要换个试试"),
    TARGET_PARAM_NOT_FOUNT(2002,"未选中任何问题或评论进行回复"),
    NO_LOGIN(2003,"未登录不能进行登录哦，请先登录"),
    SYSTEM_ERROR(2004,"服务冒烟了，请稍后再试"),
    TYPE_PARAM_WRONG(2005,"评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006,"您回复的评论不存在了，要不要换个试试"),
    CONTENT_IS_EMPTY(2007,"输入内容不能为空"),
    READ_NOTIFICATION_FAIL(2008,"禁止读别人的消息"),
    NOTIFICATION_NOT_FOUND(2009,"消息已读"),
    ;

    private String message;
    private Integer code;

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    CustomizeErrorCode(Integer code, String message) {
        this.message = message;
        this.code = code;
    }
}
