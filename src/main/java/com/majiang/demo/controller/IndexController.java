package com.majiang.demo.controller;

import com.majiang.demo.mapper.UserMapper;
import com.majiang.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 验证cookie中的token与数据库中的token
 */
public class IndexController {
    @Autowired
    private UserMapper userMapper;
    @GetMapping("/")//访问首页的时候，如下
    public String index(HttpServletRequest request){
        //浏览器获取到cookie
        Cookie[] cookies = request.getCookies();
        //遍历整个cookies数组，去找"token"(我们自己设置的那个，也就是与数据库中的相等)
        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName())){
                //找到了"token",要判断value是否和数据库中的相等
                String token = cookie.getValue();//网页上的那个
                //获取数据库中的token对应的value
                User user = userMapper.findByToken(token);
                if (user != null){
                    //数据库中有这样一个用户，就将该用户放入会话中
                    request.getSession().setAttribute("user",user);
                    //会话中中的用户不为空才会展示”月牙“而不是”登录“
                }
                break;
            }
        }
//        第一次登录的时候就将生成的token放入的cookie，如果该用户没有登录过自然是不存在这样一个token
        //拿不到当然就是登录不成功的
        return  "index";
    }
}
