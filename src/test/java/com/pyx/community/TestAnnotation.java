package com.pyx.community;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

public class TestAnnotation {

    @Test
    public void test1() throws NoSuchMethodException {
        /**
         * 通过反射获取大TestAnnotation实例
         */
        Class<TestAnnotation> clazz = TestAnnotation.class;
        Method m1 = clazz.getMethod("show");
        MyAnnotation[] mas = m1.getAnnotationsByType(MyAnnotation.class);
        for (MyAnnotation ma : mas) {
            System.out.println(ma.value());//Hello World

        }
    }

    @MyAnnotation("Hello")
    @MyAnnotation("World")
    public void show(@MyAnnotation("abc") String abc){

    }
}
