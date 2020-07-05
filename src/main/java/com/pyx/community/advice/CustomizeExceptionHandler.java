package com.pyx.community.advice;


import com.pyx.community.exception.CustomizeException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 自定义异常处理类
 */
@ControllerAdvice
public class CustomizeExceptionHandler {
    /**
     * ModelAndView功能和return返回页面一致
     */
    @ExceptionHandler(Exception.class)
    ModelAndView handle(Throwable e,
                        Model model) {
        if(e instanceof CustomizeException){
            model.addAttribute("message",e.getMessage());
        }else {
            model.addAttribute("message","服务冒烟了，请稍后再试");
        }
        return new ModelAndView("error");
    }
}
