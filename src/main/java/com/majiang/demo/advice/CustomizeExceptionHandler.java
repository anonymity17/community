package com.majiang.demo.advice;

import com.majiang.demo.exception.CustomizeException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
//用来异常处理
@ControllerAdvice
public class CustomizeExceptionHandler {
    @ExceptionHandler(Exception.class)
    ModelAndView handle(HttpServletRequest request, Throwable e, Model model){//之前写的return "index"也是默认返回的ModelAndView
        HttpStatus status = getStatus(request);
        if (e instanceof CustomizeException){
            //已经知道是自定义的异常问题
            model.addAttribute("message",e.getMessage());//取出CustomizeException中的message
        }else{
            //不明确的异常抛出这个
            model.addAttribute("message","服务器冒烟了，稍后再试哦");
        }
        return new ModelAndView("error");
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null){
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
