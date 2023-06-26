package com.majiang.demo.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

//能够拦截掉的异常可以handle（使用CustomizeExceptionHandler）
//不能handle的异常，如404，做一个通用的处理，如下

@Controller()
@RequestMapping("${server.error.path:${error.path:/error}}")
public class CustomizeErrorController implements ErrorController {//如果不自己定义就是用默认的ErrorController

    public String getErrorPath(){
        return "error";
    }
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView errorHtml(HttpServletRequest request,
                                  Model model) {
        HttpStatus status = this.getStatus(request);
        if (status.is4xxClientError()){
            //4xx客户端引起的
            model.addAttribute("message","你的请求错啦，请更换一下哦");
        }
        if (status.is5xxServerError()){
            //服务端出问题
            model.addAttribute("message","服务器冒烟了，稍后再试哦");
        }
        return new ModelAndView("error");
    }
    protected HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer)request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            try {
                return HttpStatus.valueOf(statusCode);
            } catch (Exception var4) {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
    }
}
