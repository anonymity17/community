package com.majiang.demo.interceptor;

import com.majiang.demo.mapper.UserMapper;
import com.majiang.demo.model.User;
import com.majiang.demo.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service //要让spring去接管，不然UserMapper也不能在这里自动注入
public class  SessionInterceptor implements HandlerInterceptor {
    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //在程序处理之前

        //浏览器获取到cookie
        Cookie[] cookies = request.getCookies();
        //遍历整个cookies数组，去找"token"(我们自己设置的那个，也就是与数据库中的相等)
        if (cookies != null && cookies.length != 0){
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())){
                    //找到了"token",要判断value是否和数据库中的相等
                    String token = cookie.getValue();//网页上的那个
                    //获取数据库中的token对应的value
//                    User user = userMapper.findByToken(token);//使用mybatis generator更改如下：
                    UserExample userExample = new UserExample();
                    userExample.createCriteria()
                            .andTokenEqualTo(token);//会自动的将 “token=”拼接到sql语句
                    List<User> users = userMapper.selectByExample(userExample);
                    if (users.size() != 0){
                        //数据库中有这样一个用户，就将该用户放入会话中
                        request.getSession().setAttribute("user",users.get(0));
                        //会话中中的用户不为空才会展示”月牙“而不是”登录“
                    }
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
