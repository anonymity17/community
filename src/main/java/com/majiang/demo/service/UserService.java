package com.majiang.demo.service;

import com.majiang.demo.mapper.UserMapper;
import com.majiang.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public void createOrUpdate(User user) {//user为session中的user
        //通过session中的user中的accountid去数据库中查找account_id
        User dbUser = userMapper.findByAccountId(user.getAccountId());
        if (dbUser == null){
            //说明不匹配，数据库中没有该用户
            //插入
            //插入时间
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        }else{
            //数据库中已经有了该用户，更新token
            dbUser.setGmtModified(System.currentTimeMillis());//时间变化
            dbUser.setAvatarUrl(user.getAvatarUrl());//头像可能变化
            dbUser.setName(user.getName());//名字可能变化
            //更新token
            dbUser.setToken(user.getToken());
            userMapper.update(dbUser);
        }
    }
}
