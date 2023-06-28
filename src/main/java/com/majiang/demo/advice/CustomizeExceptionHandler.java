package com.majiang.demo.advice;

import com.alibaba.fastjson.JSON;
import com.majiang.demo.dto.ResultDTO;
import com.majiang.demo.exception.CustomizeErrorCode;
import com.majiang.demo.exception.CustomizeException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.jar.JarEntry;

//用来异常处理
@ControllerAdvice
public class CustomizeExceptionHandler {
    @ExceptionHandler(Exception.class)
//    @ResponseBody //只有@ResponseBody才能返回json=>不能同时返回页面和json=>使用servlet的方式返回json(response)
//    Object handle(HttpServletRequest request, Throwable e, Model model){//之前写的return "index"也是默认返回的ModelAndView
    ModelAndView handle(HttpServletRequest request, Throwable e, Model model, HttpServletResponse response){//之前写的return "index"也是默认返回的ModelAndView
        //我们希望返回json，这样就知道一些异常信息
        //返回json和返回网页的contentType不一样，利用此来处理
        String contentType = request.getContentType();
        //1.在postman端希望打印出json，postman端的contentType为"application/JSON"，如果不转为json，拦截后postman还是一个页面
        //2.在浏览器端打印出页面
        //应该是这两个位置都呈现不同的效果
        //json中包含的信息我们可以看到错误代码来调节，而浏览器那边是给用户看的没必要看到太具体的信息
        if ("application/JSON".equals(contentType)){
            //返回JSON
            ResultDTO  resultDTO = null;
            if (e instanceof CustomizeException){
                //自定义的异常问题转为JSON
//                return ResultDTO.errorOf((CustomizeException) e);
                resultDTO = ResultDTO.errorOf((CustomizeException) e);

            }else{
                //不明确的异常转JSON,服务器短的异常
//                return ResultDTO.errorOf(CustomizeErrorCode.SYS_ERROR);
                resultDTO = ResultDTO.errorOf(CustomizeErrorCode.SYS_ERROR);
            }
            response.setContentType("application/JSON");
            response.setStatus(200);
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = null;
            try {
                writer = response.getWriter();
            } catch (IOException ex) {

            }
            writer.write(JSON.toJSONString(resultDTO));
            writer.close();
            return null;
        }else{
            //返回页面
            if (e instanceof CustomizeException){
                //自定义的异常
                model.addAttribute("message",e.getMessage());
            }else{
                //不明确的异常转JSON,服务器短的异常
                model.addAttribute("message",CustomizeErrorCode.SYS_ERROR.getMessage());
            }
        }
        return new ModelAndView("error");
    }
}
