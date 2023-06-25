package com.majiang.demo.controller;

import com.majiang.demo.dto.AccessTokenDTO;
import com.majiang.demo.dto.GithubUser;
import com.majiang.demo.mapper.UserMapper;
import com.majiang.demo.model.User;
import com.majiang.demo.provider.GithubProvider;
import com.majiang.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;
    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.redirect.url}")
    private String redirectUrl;
    @Value("${github.client.secret}")
    private String clientSecret;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;

    @GetMapping("/callback")
    public String callback(@RequestParam(name="code") String code,
                           @RequestParam(name="state") String state,
                           HttpServletRequest request,
                           HttpServletResponse response){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setRedirect_uri(redirectUrl);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setState(state);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);
        System.out.println("githubUser" + githubUser);
        if (githubUser != null && githubUser.getId() != null){//当数据库中已经存在该用户，不应该新建user，而是更新
//            第一次登录
            //获取到用户的数据，并且该数据不为空，就可以将用户放入会话
            //当会话中存在了该用户，才会将"登录"变成"月牙"
            request.getSession().setAttribute("user",githubUser);
            User user = new User();
            //登录成功生成token
            String token = UUID.randomUUID().toString();
            //将token以及其他的一些信息存储到数据库中
            user.setToken(token);//Java 中生成随机唯一标识符 (UUID) 的常用方法之一。UUID 是一种标准的 128 位数字格式，用于在分布式系统中唯一标识实体。
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setName(githubUser.getName());
            user.setAvatarUrl(githubUser.getAvatarUrl());
//            登录数据库，如果数据库中存在的account_id等于我登录时的account_id,更新数据库中的token，如果不存在就做插入操作
            userService.createOrUpdate(user);
            //将token放到cookie里面
            response.addCookie(new Cookie("token",token));
            return "redirect:/";//将地址重定向到index
        }else{
            //登陆失败，重新登陆
            return "redirect:/";//将地址重定向到index
        }
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response){
        //删除session
        //移除user
        request.getSession().removeAttribute("user");
        //移除cookie
        Cookie cookie = new Cookie("token",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }
}
