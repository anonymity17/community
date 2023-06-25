//package com.majiang.demo.advice;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//
//@ControllerAdvice
//public class CustomizeExceptionHandler {
//    @ExceptionHandler(Exception.class)
//    ModelAndView handle(HttpServletRequest request, Throwable ex, Model model){
//        HttpStatus status = getStatus(request);
//        model.addAttribute("服务器冒烟了，稍后再试哦");
//        return new ModelAndView("error");
//    }
//
//    private HttpStatus getStatus(HttpServletRequest request) {
//        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
//        if (statusCode == null){
//            return HttpStatus.INTERNAL_SERVER_ERROR;
//        }
//        return HttpStatus.valueOf(statusCode);
//
//    }
//}
