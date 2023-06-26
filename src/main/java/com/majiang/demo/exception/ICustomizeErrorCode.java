package com.majiang.demo.exception;
//定义一个接口，不同类型的异常可以分别写(都实现这个接口)，不用全部写在一个类中
public interface ICustomizeErrorCode {
    String getMessage();
}
