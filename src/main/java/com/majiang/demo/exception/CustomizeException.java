package com.majiang.demo.exception;
//可以通过自己定义的异常很好地处理上下文中的业务异常
//要使用RuntimeException向上抛，之后在CustomizeExceptionHandler中拦截
public class CustomizeException extends RuntimeException{
    private String message;

    public CustomizeException(ICustomizeErrorCode errorCode){
        this.message = errorCode.getMessage();
    }
    public CustomizeException(String message){
        this.message = message;
    }
    @Override
    public String getMessage(){
        return message;
    }
}
